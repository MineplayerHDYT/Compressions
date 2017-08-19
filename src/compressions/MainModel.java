//==================================================================================================

    package compressions;

//==================================================================================================

    import com.google.gson.JsonObject;
    import com.google.gson.JsonParser;
    import compressions.MainItem.Position;

//==================================================================================================

    import net.minecraft.block.Block;
    import net.minecraft.block.BlockGravel;
    import net.minecraft.block.state.IBlockState;
    import net.minecraft.client.Minecraft;
    import net.minecraft.client.renderer.block.model.*;
    import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
    import net.minecraft.client.renderer.texture.TextureAtlasSprite;
    import net.minecraft.client.renderer.vertex.VertexFormat;
    import net.minecraft.client.resources.IResourceManager;
    import net.minecraft.creativetab.CreativeTabs;
    import net.minecraft.entity.EntityLivingBase;
    import net.minecraft.init.Blocks;
    import net.minecraft.init.Items;
    import net.minecraft.item.Item;
    import net.minecraft.item.ItemBlock;
    import net.minecraft.item.ItemStack;
    import net.minecraft.nbt.JsonToNBT;
    import net.minecraft.nbt.NBTTagCompound;
    import net.minecraft.util.EnumFacing;
    import net.minecraft.util.NonNullList;
    import net.minecraft.util.ResourceLocation;
    import net.minecraft.util.math.BlockPos;
    import net.minecraft.world.World;
    import net.minecraftforge.client.ForgeHooksClient;
    import net.minecraftforge.client.event.ModelBakeEvent;
    import net.minecraftforge.client.event.ModelRegistryEvent;
    import net.minecraftforge.client.model.*;
    import net.minecraftforge.common.model.IModelState;
    import net.minecraftforge.common.model.TRSRTransformation;
    import net.minecraftforge.common.property.IExtendedBlockState;
    import net.minecraftforge.common.property.IUnlistedProperty;
    import net.minecraftforge.fml.common.Mod;
    import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
    import net.minecraftforge.fml.common.registry.ForgeRegistries;
    import org.apache.commons.lang3.tuple.ImmutablePair;
    import org.apache.commons.lang3.tuple.Pair;
    import org.lwjgl.BufferUtils;
    import org.lwjgl.opengl.GL11;
    import org.lwjgl.util.vector.Vector3f;

    import javax.annotation.Nullable;
    import javax.vecmath.Matrix4f;
    import java.nio.FloatBuffer;
    import java.util.ArrayList;
    import java.util.Collections;
    import java.util.List;
    import java.util.function.Function;

    import static org.lwjgl.opengl.GL11.glMultMatrix;

//==================================================================================================
    @Mod.EventBusSubscriber
//==================================================================================================

    public class MainModel implements ICustomModelLoader, IModel , IBakedModel
        /* implements ICustomModelLoader, IModel, IBakedModel */ {
    //==============================================================================================

        public static MainModel INSTANCE = new MainModel( null , null , null );

        public static Overrrides overrrides = new Overrrides( Collections.EMPTY_LIST );

    //==============================================================================================

        public ItemStack stack = null;
        public World world = null;
        public EntityLivingBase entity = null;

        MainModel( ItemStack stack , World world , EntityLivingBase entity ) {

            this.world = world;
            this.entity = entity;

            if( null == stack ) return;

            if( stack.hasTagCompound() ) {

                NBTTagCompound tag = stack.getTagCompound();
                String  mod   = tag.getString( "Mod" );
                String  entry = tag.getString( "Entry" );
                Integer meta  = tag.getInteger( "Meta" );
                String  nbt   = tag.getString( "NBT" );

                List<Item> items = new ArrayList<>( ForgeRegistries.ITEMS.getValues() );

                items.removeIf( s -> !s.getRegistryName().getResourceDomain().equals( mod ) );
                items.removeIf( s -> !s.getRegistryName().getResourcePath  ().equals( entry ) );

                for( Item item : items ) {

                    CreativeTabs tab = item.getCreativeTab();
                    if( null == tab ) tab = CreativeTabs.CREATIVE_TAB_ARRAY[0];

                    //----------------------------------------------------------------------------------

                    NonNullList<ItemStack> stacks = NonNullList.create();
                    item.getSubItems( tab , stacks );

                    stacks.removeIf( s -> !meta.equals( s.getMetadata() ) );

                    if( !nbt.isEmpty() ) stacks.removeIf( s -> !s.hasTagCompound() );
                    if( !nbt.isEmpty() )
                        stacks.removeIf( s -> !s.getTagCompound().toString()
                                .replace( " " , "" ).toLowerCase()
                                .equals( nbt.replace( " " , "" )
                                        .toLowerCase() ) );

                    if( 1 == stacks.size() ) this.stack = stacks.get( 0 );
                }
            }
        }

    //==============================================================================================

        public static class Overrrides extends ItemOverrideList {

            public Overrrides(List<ItemOverride> overridesIn) {
                super(overridesIn);
            }

            @Override
            public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity)
            {
                return new MainModel( stack , world , entity );
            }
        }

    //==============================================================================================
    // ICustomModelLoader section
    //==============================================================================================

        @Override public boolean accepts( ResourceLocation modelLocation ) {
        //------------------------------------------------------------------------------------------

            return modelLocation.getResourceDomain().equals( Base.modId );

        //------------------------------------------------------------------------------------------
        }

        @Override public IModel loadModel( ResourceLocation modelLocation ) throws Exception {
        //------------------------------------------------------------------------------------------

            return MainModel.INSTANCE;

        //------------------------------------------------------------------------------------------
        }

        @Override public void onResourceManagerReload( IResourceManager resourceManager ) {
        //------------------------------------------------------------------------------------------

        //------------------------------------------------------------------------------------------
        }

    //==============================================================================================
    // IModel section
    //==============================================================================================

        @Override public IBakedModel bake( IModelState state ,
                                           VertexFormat format ,
            Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter ) {
        //------------------------------------------------------------------------------------------

            return MainModel.INSTANCE;

        //------------------------------------------------------------------------------------------
        }

    //==============================================================================================
    // IBakedModel section
    //==============================================================================================

        @Override public List<BakedQuad> getQuads( @Nullable IBlockState state ,
                                                   @Nullable EnumFacing side   ,
                                                             long rand         ) {

            //org.lwjgl.input.Mouse.setGrabbed(false);

            Item gravel = Item.getItemFromBlock( Blocks.GRAVEL );

            ItemStack stack = null == this.stack ? new ItemStack( gravel , 1 , 0 ) : this.stack;

            if( state instanceof IExtendedBlockState ) {

                IExtendedBlockState exstate = (IExtendedBlockState) state;

                Integer posx = null;
                Integer posy = null;
                Integer posz = null;

                for( IUnlistedProperty prop : exstate.getUnlistedNames() ) {
                    if( prop.getName().equals( "PosX") ) posx = (Integer) exstate.getValue( prop );
                    if( prop.getName().equals( "PosY") ) posy = (Integer) exstate.getValue( prop );
                    if( prop.getName().equals( "PosZ") ) posz = (Integer) exstate.getValue( prop );
                }

                BlockPos pos      = new BlockPos( posx , posy, posz );
                Position position = new Position( posx , posy , posz);

                if( MainItem.placed.containsKey( position ) )
                    if( MainItem.placed.get( position ).getLeft().isAirBlock( pos ) )
                        MainItem.placed.remove( position );

                ItemStack newStack = MainItem.placed.getOrDefault( position , new ImmutablePair<>
                        (null , null) ).getRight();

                if( null != newStack ) {

                    NBTTagCompound tag = newStack.getTagCompound();
                    String  mod   = tag.getString( "Mod" );
                    String  entry = tag.getString( "Entry" );
                    Integer meta  = tag.getInteger( "Meta" );
                    String  nbt   = tag.getString( "NBT" );

                    List<Item> items = new ArrayList<>( ForgeRegistries.ITEMS.getValues() );

                    items.removeIf( s -> !s.getRegistryName().getResourceDomain().equals( mod ) );
                    items.removeIf( s -> !s.getRegistryName().getResourcePath  ().equals( entry ) );

                    for( Item item : items ) {

                        CreativeTabs tab = item.getCreativeTab();
                        if( null == tab ) tab = CreativeTabs.CREATIVE_TAB_ARRAY[0];

                        //----------------------------------------------------------------------------------

                        NonNullList<ItemStack> stacks = NonNullList.create();
                        item.getSubItems( tab , stacks );

                        stacks.removeIf( s -> !meta.equals( s.getMetadata() ) );

                        if( !nbt.isEmpty() ) stacks.removeIf( s -> !s.hasTagCompound() );
                        if( !nbt.isEmpty() )
                            stacks.removeIf( s -> !s.getTagCompound().toString()
                                    .replace( " " , "" ).toLowerCase()
                                    .equals( nbt.replace( " " , "" )
                                            .toLowerCase() ) );

                        if( 1 == stacks.size() ) stack = stacks.get( 0 );
                    }
                }

                int h = 0;
            }

            Pair<? extends IBakedModel,Matrix4f> tr = this.handlePerspective(  stack , TransformType
                    .GUI );

            ForgeHooksClient.multiplyCurrentGlMatrix( tr.getRight() );

            return tr.getLeft().getQuads( Block.getBlockFromItem( stack.getItem() ).getDefaultState(),
                    side , rand );



            //model = model.getOverrides().handleItemState( model , stack , world , entity );

            /*
            Pair<? extends IBakedModel, Matrix4f> tr = model.handlePerspective(
                    ItemCameraTransforms.TransformType.GUI );

            ForgeHooksClient.multiplyCurrentGlMatrix( tr.getRight() );

            return tr.getLeft().getQuads( Block.getBlockFromItem( stack.getItem() ).getDefaultState(),
                    side , rand );//*/
/*
            model = ForgeHooksClient.handleCameraTransforms( model ,
                    ItemCameraTransforms.TransformType.GUI , false );

            Matrix4f scale = new Matrix4f();
            scale.setIdentity();
            scale.setScale( 1.5f );

            Matrix4f matrix = new Matrix4f();
            matrix.setIdentity();
            matrix.mul( scale );

            ForgeHooksClient.multiplyCurrentGlMatrix( matrix );//*/

/*

            Matrix4f translate = new Matrix4f();
            translate.setIdentity();
            translate.setTranslation(new Vector3f( 0.0f , -1.0f / 16, -1.0f / 16 ));

            Matrix4f rotX = new Matrix4f();
            rotX.setIdentity();
            rotX.setRotation( new AxisAngle4f( 1, 0, 0, (float) Math.toRadians( 360 - 45 ) ) );

            Matrix4f rotY = new Matrix4f();
            rotY.setIdentity();
            rotY.setRotation( new AxisAngle4f( 0, 1, 0, (float) Math.toRadians( 360 - 45 ) ) );

            Matrix4f scale = new Matrix4f();
            scale.setIdentity();
            scale.setScale( 1.0f - 1.0f / 16 );

            Matrix4f matrix = new Matrix4f();
            matrix.setIdentity();
            //matrix.mul( translate );
            //matrix.mul( scale );
            //matrix.mul( rotX );
            //matrix.mul( rotY );

            ForgeHooksClient.multiplyCurrentGlMatrix( matrix );
            //*/

            //return model.getQuads( Block.getBlockFromItem( stack.getItem() ).getDefaultState(),
            //        side , rand );
        }

        @Override public boolean isAmbientOcclusion() {

            Item gravel = Item.getItemFromBlock( Blocks.GRAVEL );

            ItemStack stack = null == this.stack ? new ItemStack( gravel , 1 , 0 ) : this.stack;

            IBakedModel model = Minecraft.getMinecraft()
                    .getRenderItem()
                    .getItemModelMesher()
                    .getItemModel( stack );

            return model.isAmbientOcclusion();
        }

        @Override public boolean isGui3d() {

            Item gravel = Item.getItemFromBlock( Blocks.GRAVEL );

            ItemStack stack = null == this.stack ? new ItemStack( gravel , 1 , 0 ) : this.stack;

            IBakedModel model = Minecraft.getMinecraft()
                    .getRenderItem()
                    .getItemModelMesher()
                    .getItemModel( stack );

            return model.isGui3d();
        }

        @Override public boolean isBuiltInRenderer() {


            Item gravel = Item.getItemFromBlock( Blocks.GRAVEL );

            ItemStack stack = null == this.stack ? new ItemStack( gravel , 1 , 0 ) : this.stack;

            IBakedModel model = Minecraft.getMinecraft()
                    .getRenderItem()
                    .getItemModelMesher()
                    .getItemModel( stack );

            return model.isBuiltInRenderer();
        }

        @Override public TextureAtlasSprite getParticleTexture() {

            Item gravel = Item.getItemFromBlock( Blocks.GRAVEL );

            ItemStack stack = null == this.stack ? new ItemStack( gravel , 1 , 0 ) : this.stack;

            IBakedModel model = Minecraft.getMinecraft()
                    .getRenderItem()
                    .getItemModelMesher()
                    .getItemModel( stack );

            return model.getParticleTexture();
        }

        @Override public ItemOverrideList getOverrides() {
            //org.lwjgl.input.Mouse.setGrabbed(false);

            return new Overrrides( Collections.EMPTY_LIST );
        }

        @Override public Pair<? extends IBakedModel,Matrix4f> handlePerspective(TransformType type){

            return this.handlePerspective( this.stack , type );
        }

        public Pair<? extends IBakedModel,Matrix4f> handlePerspective(
                ItemStack stack ,
                TransformType type) {
            IBakedModel model = Minecraft.getMinecraft()
                    .getRenderItem()
                    .getItemModelMesher()
                    .getItemModel( stack );

            //org.lwjgl.input.Mouse.setGrabbed(false);

            org.lwjgl.util.vector.Vector3f rot1= ((PerspectiveMapWrapper) model)
                    .getItemCameraTransforms().gui.rotation;

            Matrix4f matrix = new Matrix4f();
            matrix.setIdentity();
            // 30 , 225 , 0
            // 0.5 , 0.5 , 0.5

            if( type.equals( TransformType.GUI ) ) {
                Vector3f rotation = ((PerspectiveMapWrapper) model)
                        .getItemCameraTransforms().gui.rotation;
                Vector3f translation = ((PerspectiveMapWrapper) model)
                        .getItemCameraTransforms().gui.translation;
                Vector3f scale = ((PerspectiveMapWrapper) model)
                        .getItemCameraTransforms().gui.scale;

                Matrix4f rotX = new Matrix4f();
                rotX.setIdentity();
                rotX.rotX( (float) Math.toRadians( rotation.getX() ) );

                Matrix4f rotY = new Matrix4f();
                rotY.setIdentity();
                rotY.rotY( (float) Math.toRadians( rotation.getY() ) );

                Matrix4f mat = new Matrix4f();
                mat.setIdentity();
                mat.mul( rotX );
                mat.mul( rotY );
                mat.setScale( scale.getX() );

                TRSRTransformation tr1 = new TRSRTransformation(mat);

                TRSRTransformation tr2 = TRSRTransformation.blockCornerToCenter( tr1 );
                matrix = tr1.getMatrix();
            }

            return new ImmutablePair<>( model , matrix );
        }

    //==============================================================================================
        @SubscribeEvent
    //==============================================================================================

        public static void Register( ModelRegistryEvent event ) {
        //------------------------------------------------------------------------------------------

            ModelLoaderRegistry.registerLoader( MainModel.INSTANCE );

        //------------------------------------------------------------------------------------------

            ResourceLocation itemRL  = MainItem.instance.getRegistryName();
            ResourceLocation blockRL = MainBlock.instance.getRegistryName();

            ModelResourceLocation itemMRL  = new ModelResourceLocation( itemRL  , "inventory" );
            ModelResourceLocation blockMRL = new ModelResourceLocation( blockRL , "inventory" );

        //------------------------------------------------------------------------------------------

            ModelLoader.setCustomModelResourceLocation( MainItem.instance , 0 , itemMRL  );
            ModelLoader.setCustomModelResourceLocation( MainItem.instance , 0 , blockMRL );

        //------------------------------------------------------------------------------------------
        }



    //==============================================================================================
    }

//==================================================================================================
