//==================================================================================================

    package compressions;

//==================================================================================================

    import net.minecraft.block.Block;
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
    import net.minecraft.item.Item;
    import net.minecraft.item.ItemStack;
    import net.minecraft.nbt.NBTTagCompound;
    import net.minecraft.util.EnumFacing;
    import net.minecraft.util.NonNullList;
    import net.minecraft.util.ResourceLocation;
    import net.minecraft.util.math.BlockPos;
    import net.minecraft.world.World;
    import net.minecraftforge.client.ForgeHooksClient;
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
    import org.lwjgl.util.vector.Vector3f;

//==================================================================================================

    import javax.annotation.Nullable;
    import javax.vecmath.Matrix4f;
    import java.util.*;
    import java.util.function.Function;

//==================================================================================================
    @Mod.EventBusSubscriber @SuppressWarnings( { "WeakerAccess" , "CanBeFinal" } )
//==================================================================================================

    public class MainModel {

    //==============================================================================================
        public static Map<String , IBakedModel> items = new HashMap<>();
    //==============================================================================================


        public static class Overrides extends ItemOverrideList {

        //==========================================================================================

            public Overrides( List<ItemOverride> overrides ) {
            //--------------------------------------------------------------------------------------
                super( overrides );
            //--------------------------------------------------------------------------------------

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public IBakedModel handleItemState( IBakedModel                originalModel
                                                        , ItemStack                  stack
                                                        , @Nullable World            world
                                                        , @Nullable EntityLivingBase entity ) {
            //--------------------------------------------------------------------------------------

                String         mod   = stack.getTagCompound().getString( "Mod" );
                String         entry = stack.getTagCompound().getString( "Entry" );
                Integer        meta  = stack.getTagCompound().getInteger( "Meta" );
                NBTTagCompound nbt   = stack.getTagCompound().getCompoundTag( "NBT" );

            //--------------------------------------------------------------------------------------
                String ID = mod + entry + meta + nbt;
            //--------------------------------------------------------------------------------------

                if( items.containsKey( ID ) ) return items.get( ID );

            //--------------------------------------------------------------------------------------
                List<ItemStack> stacks = new ArrayList<>( Configurations.entries );
            //--------------------------------------------------------------------------------------

                stacks.removeIf( s -> !s.getItem()
                                        .getRegistryName()
                                        .getResourceDomain()
                                        .equals( mod ) );

                stacks.removeIf( s -> !s.getItem()
                                        .getRegistryName()
                                        .getResourcePath()
                                        .equals( entry ) );

                stacks.removeIf( s -> !meta.equals( s.getMetadata() ) );

                stacks.removeIf( s -> !s.getTagCompound()
                                        .toString()
                                        .replace( " " , "" )
                                        .toLowerCase()
                                        .contains(   nbt.toString()
                                                        .replace( " " , "" )
                                                        .toLowerCase() ) );

            //--------------------------------------------------------------------------------------
                if( 1 != stacks.size() ) return originalModel;
            //--------------------------------------------------------------------------------------

                items.put( ID , new CompressedBakedModel( stack , CompressedModelState ) );

            //--------------------------------------------------------------------------------------
                return items.get( ID );
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

        }


    //==============================================================================================
        public static Overrides overrides = new Overrides( Collections.EMPTY_LIST );
    //==============================================================================================


        public static class CompressedBakedModel extends PerspectiveMapWrapper {

        //==========================================================================================

            public ItemStack             baseStack;
            public PerspectiveMapWrapper baseModel;

        //==========================================================================================

            CompressedBakedModel( ItemStack base , IModelState state ) {
            //--------------------------------------------------------------------------------------
                super( Minecraft.getMinecraft()
                                .getRenderItem()
                                .getItemModelMesher()
                                .getItemModel( base ) , state );
            //--------------------------------------------------------------------------------------

                this.baseStack = base;
                this.baseModel = new PerspectiveMapWrapper( Minecraft.getMinecraft()
                                                                      .getRenderItem()
                                                                      .getItemModelMesher()
                                                                      .getItemModel( baseStack )
                                                           , state );

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public ItemOverrideList getOverrides() { return overrides; }

            @Override public List<BakedQuad>  getQuads( @Nullable IBlockState inState ,
                                                        @Nullable EnumFacing  side  ,
                                                                  long        rand  ) {
            //--------------------------------------------------------------------------------------
                if( !baseStack.hasTagCompound() ) {
            //--------------------------------------------------------------------------------------

                    Block block = Block.getBlockFromItem( this.baseStack.getItem() );

                    Pair<IBakedModel , Matrix4f> tr = this.handlePerspective( TransformType.GUI );
                    ForgeHooksClient.multiplyCurrentGlMatrix( tr.getRight() );

                    return tr.getLeft().getQuads( block.getDefaultState() , side , rand );

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------

                IExtendedBlockState state = (IExtendedBlockState) inState;

                Integer posX = null;
                Integer posY = null;
                Integer posZ = null;

                for( IUnlistedProperty prop : state.getUnlistedNames() ) {
                    if( prop.getName().equals( "PosX") ) posX = (Integer) state.getValue( prop );
                    if( prop.getName().equals( "PosY") ) posY = (Integer) state.getValue( prop );
                    if( prop.getName().equals( "PosZ") ) posZ = (Integer) state.getValue( prop );
                }

                BlockPos position = new BlockPos( posX , posY , posZ );

            //--------------------------------------------------------------------------------------

                Integer     dimID  = Minecraft.getMinecraft().world.provider.getDimension();
                ItemStack   placed = MainItem.placed.get( dimID ).get( position );
                IBakedModel model  = overrides.handleItemState( this , placed , null , null );

            //--------------------------------------------------------------------------------------
                return model.getQuads( inState , side, rand );
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public Pair<IBakedModel, Matrix4f> handlePerspective(TransformType type) {
            //--------------------------------------------------------------------------------------

                Vector3f rotation    = new Vector3f( 0 , 0 , 0 );
                Vector3f translation = new Vector3f( 0 , 0 , 0 );
                Vector3f scale       = new Vector3f( 1 , 1 , 1 );

            //--------------------------------------------------------------------------------------
                if( type.equals( TransformType.GUI ) ) {
            //--------------------------------------------------------------------------------------

                    rotation    = baseModel.getItemCameraTransforms().gui.rotation;
                    translation = baseModel.getItemCameraTransforms().gui.translation;
                    scale       = baseModel.getItemCameraTransforms().gui.scale;

            //--------------------------------------------------------------------------------------
                } if( type.equals( TransformType.FIRST_PERSON_RIGHT_HAND ) ) {
            //--------------------------------------------------------------------------------------

                    rotation    = baseModel.getItemCameraTransforms().firstperson_right.rotation;
                    translation = baseModel.getItemCameraTransforms().firstperson_right.translation;
                    scale       = baseModel.getItemCameraTransforms().firstperson_right.scale;

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------

                Matrix4f rotX = new Matrix4f();
                Matrix4f rotY = new Matrix4f();
                Matrix4f rotZ = new Matrix4f();

                rotX.setIdentity();
                rotY.setIdentity();
                rotZ.setIdentity();

                rotX.rotX( (float) Math.toRadians( rotation.getX() ) );
                rotY.rotX( (float) Math.toRadians( rotation.getY() ) );
                rotZ.rotX( (float) Math.toRadians( rotation.getZ() ) );

            //--------------------------------------------------------------------------------------

                Matrix4f scaleM = new Matrix4f();
                Matrix4f transM = new Matrix4f();

                scaleM.setIdentity();
                transM.setIdentity();

                scaleM.setM00( scale.getX() );
                scaleM.setM11( scale.getY() );
                scaleM.setM22( scale.getZ() );

                transM.setM03( translation.getX() );
                transM.setM13( translation.getY() );
                transM.setM23( translation.getZ() );

            //--------------------------------------------------------------------------------------
                Matrix4f matrix = new Matrix4f();
            //--------------------------------------------------------------------------------------

                matrix.setIdentity();

                matrix.mul( rotX );
                matrix.mul( rotY );
                matrix.mul( rotZ );

                matrix.mul( scaleM );
                matrix.mul( transM );

                //TRSRTransformation tr1 = new TRSRTransformation(mat);
                //matrix = tr1.getMatrix();

                return new ImmutablePair<>( baseModel , matrix );
            }

        //==========================================================================================

        }


    //==============================================================================================


        public static class CompressedModel implements IModel {

        //==========================================================================================
            @Override
        //==========================================================================================

            public IBakedModel bake( IModelState  state
                                   , VertexFormat format
                                   , Function< ResourceLocation
                                             , TextureAtlasSprite > bakedTextureGetter ) {
            //--------------------------------------------------------------------------------------
                ItemStack gravel = new ItemStack( Item.getItemFromBlock( Blocks.GRAVEL ) , 1 , 0 );
            //--------------------------------------------------------------------------------------

                CompressedModelState = state;

                return new CompressedBakedModel( gravel , state );

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
    }


    //==============================================================================================
        public static IModelState CompressedModelState = null;
    //==============================================================================================


        public static class CompressedModelLoader implements ICustomModelLoader {
        //==========================================================================================

            @Override public boolean accepts( ResourceLocation modelLocation ) {
            //--------------------------------------------------------------------------------------

                return modelLocation.getResourceDomain().equals( Base.modId );

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public IModel loadModel(ResourceLocation modelLocation) throws Exception {
            //--------------------------------------------------------------------------------------

                return new CompressedModel();

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public void onResourceManagerReload( IResourceManager resourceManager ) {
            //--------------------------------------------------------------------------------------

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

        }


    //==============================================================================================

    //==============================================================================================
    // IBakedModel section
    //==============================================================================================
/*
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


//            Pair<? extends IBakedModel, Matrix4f> tr = model.handlePerspective(
//                    ItemCameraTransforms.TransformType.GUI );
//
//            ForgeHooksClient.multiplyCurrentGlMatrix( tr.getRight() );
//
//            return tr.getLeft().getQuads( Block.getBlockFromItem( stack.getItem() ).getDefaultState(),
//                    side , rand );

//            model = ForgeHooksClient.handleCameraTransforms( model ,
//                    ItemCameraTransforms.TransformType.GUI , false );
//
//            Matrix4f scale = new Matrix4f();
//            scale.setIdentity();
//            scale.setScale( 1.5f );
//
//            Matrix4f matrix = new Matrix4f();
//            matrix.setIdentity();
//            matrix.mul( scale );
//
//            ForgeHooksClient.multiplyCurrentGlMatrix( matrix );


//
//            Matrix4f translate = new Matrix4f();
//            translate.setIdentity();
//            translate.setTranslation(new Vector3f( 0.0f , -1.0f / 16, -1.0f / 16 ));
//
//            Matrix4f rotX = new Matrix4f();
//            rotX.setIdentity();
//            rotX.setRotation( new AxisAngle4f( 1, 0, 0, (float) Math.toRadians( 360 - 45 ) ) );
//
//            Matrix4f rotY = new Matrix4f();
//            rotY.setIdentity();
//            rotY.setRotation( new AxisAngle4f( 0, 1, 0, (float) Math.toRadians( 360 - 45 ) ) );
//
//            Matrix4f scale = new Matrix4f();
//            scale.setIdentity();
//            scale.setScale( 1.0f - 1.0f / 16 );
//
//            Matrix4f matrix = new Matrix4f();
//            matrix.setIdentity();
//            //matrix.mul( translate );
//            //matrix.mul( scale );
//            //matrix.mul( rotX );
//            //matrix.mul( rotY );
//
//            ForgeHooksClient.multiplyCurrentGlMatrix( matrix );

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

            return overrrides;
        }

        @Override public Pair<? extends IBakedModel,Matrix4f> handlePerspective(TransformType type){

            return this.handlePerspective( this.stack , type );
        }

        public Pair<? extends IBakedModel,Matrix4f> handlePerspective(
                ItemStack stack ,
                TransformType type) {

            Item gravel = Item.getItemFromBlock( Blocks.GRAVEL );

            stack = null == stack ? new ItemStack( gravel , 1 , 0 ) : stack;

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
//*/
    //==============================================================================================
        @SubscribeEvent
    //==============================================================================================

        public static void Register( ModelRegistryEvent event ) {
        //------------------------------------------------------------------------------------------

            ModelLoaderRegistry.registerLoader( new CompressedModelLoader() );

        //------------------------------------------------------------------------------------------

            ResourceLocation itemRL  = MainItem.controlCMP.getRegistryName();
            ResourceLocation blockRL = MainBlock.controlCMP.getRegistryName();

            ModelResourceLocation itemMRL  = new ModelResourceLocation( itemRL  , "inventory" );
            ModelResourceLocation blockMRL = new ModelResourceLocation( blockRL , "inventory" );

        //------------------------------------------------------------------------------------------

            ModelLoader.setCustomModelResourceLocation( MainItem.controlCMP , 0 , itemMRL  );
            ModelLoader.setCustomModelResourceLocation( MainItem.controlCMP , 0 , blockMRL );

        //------------------------------------------------------------------------------------------
        }



    //==============================================================================================
    }

//==================================================================================================
