//==================================================================================================

    package compressions;

//==================================================================================================

    import mcp.MethodsReturnNonnullByDefault;
    import net.minecraft.block.material.Material;
    import net.minecraft.block.state.IBlockState;
    import net.minecraft.client.Minecraft;
    import net.minecraft.client.renderer.*;
    import net.minecraft.client.renderer.block.model.BakedQuad;
    import net.minecraft.client.renderer.block.model.IBakedModel;
    import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
    import net.minecraft.client.renderer.block.model.ModelResourceLocation;
    import net.minecraft.client.renderer.texture.TextureAtlasSprite;
    import net.minecraft.client.renderer.texture.TextureMap;
    import net.minecraft.client.renderer.vertex.VertexFormat;
    import net.minecraft.creativetab.CreativeTabs;
    import net.minecraft.entity.EntityLivingBase;
    import net.minecraft.inventory.InventoryCrafting;
    import net.minecraft.item.EnumDyeColor;
    import net.minecraft.item.ItemStack;
    import net.minecraft.item.crafting.IRecipe;
    import net.minecraft.item.crafting.Ingredient;
    import net.minecraft.nbt.NBTTagCompound;
    import net.minecraft.util.EnumFacing;
    import net.minecraft.util.NonNullList;
    import net.minecraft.util.ResourceLocation;
    import net.minecraft.util.math.BlockPos;
    import net.minecraft.world.World;
    import net.minecraftforge.client.ForgeHooksClient;
    import net.minecraftforge.client.event.ModelRegistryEvent;
    import net.minecraftforge.client.event.TextureStitchEvent;
    import net.minecraftforge.client.model.IModel;
    import net.minecraftforge.client.model.ModelLoader;
    import net.minecraftforge.client.model.ModelLoaderRegistry;
    import net.minecraftforge.common.config.Configuration;
    import net.minecraftforge.common.model.IModelState;
    import net.minecraftforge.event.RegistryEvent.Register;
    import net.minecraftforge.fml.client.registry.ClientRegistry;
    import net.minecraftforge.fml.common.Mod;
    import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
    import org.lwjgl.opengl.GL11;

//==================================================================================================

    import javax.annotation.Nullable;
    import javax.annotation.ParametersAreNonnullByDefault;
    import java.io.File;
    import java.nio.file.Paths;
    import java.util.*;
    import java.util.function.Function;

//==================================================================================================
    @Mod.EventBusSubscriber @MethodsReturnNonnullByDefault @ParametersAreNonnullByDefault
    @SuppressWarnings( { "WeakerAccess" , "CanBeFinal" , "unused" } )
//==================================================================================================

    public class Compressed extends Base {
    //==============================================================================================
    // Unique identification
    //==============================================================================================

        public static String ID = "compressed";

    //==============================================================================================
    // Entry section
    //==============================================================================================

        /*******************************************************************************************
         * @see compressions.Base.Entry
         *******************************************************************************************
         *
         *   A entry representing a compressed BLOCK
         *
         ******************************************************************************************/

        public static class Entry extends Base.Entry<Entry> {

        //==========================================================================================

            public Integer Width;
            public Integer Height;

        //==========================================================================================
        // Unique identification
        //==========================================================================================

            @Override public boolean equals( Object object ) {
            //--------------------------------------------------------------------------------------
                if( !( object instanceof Entry ) ) return false;
            //--------------------------------------------------------------------------------------

                Entry other = (Entry) object;

            //--------------------------------------------------------------------------------------

                if( null == this.Width && null != other.Width ) return false;
                if( null != this.Width && null == other.Width ) return false;
                if( null != this.Width && !this.Width.equals( other.Width ) ) return false;

                if( null == this.Height && null != other.Height ) return false;
                if( null != this.Height && null == other.Height ) return false;
                if( null != this.Height && !this.Height.equals( other.Height ) ) return false;

            //--------------------------------------------------------------------------------------
                return super.equals( object );
            //--------------------------------------------------------------------------------------
            }

            @Override public int hashCode() {
            //--------------------------------------------------------------------------------------

                return  ( null == Width   ? "".hashCode() : Width.hashCode()  ) ^
                        ( null == Height  ? "".hashCode() : Height.hashCode() ) ^ super.hashCode();

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
        // Setup
        //==========================================================================================

            Entry() {}

            Entry( @Nullable Entry other ) {
            //--------------------------------------------------------------------------------------
                super( other ); if( null == other ) return;
            //--------------------------------------------------------------------------------------

                this.Width = other.Width;
                this.Height = other.Height;

            //--------------------------------------------------------------------------------------
            }

            Entry( ItemStack stack ) {
            //--------------------------------------------------------------------------------------
                super( stack );
            //--------------------------------------------------------------------------------------
            }

            Entry( NBTTagCompound tag ) {
            //--------------------------------------------------------------------------------------
                super( tag );
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
            public BakedModel model;
        //==========================================================================================

            public ItemStack getCompressed() {
            //--------------------------------------------------------------------------------------

                ItemStack stack = new ItemStack( ITEMBLOCK , 1 , 0 );

            //--------------------------------------------------------------------------------------
                NBTTagCompound tag = this.toTag();
            //--------------------------------------------------------------------------------------

                stack.setTagCompound( this.toTag() );

            //--------------------------------------------------------------------------------------

                stack.setStackDisplayName( "" + tag.getInteger( "Height" ) + "x"
                                              + tag.getInteger( "Width" ) + " "
                                              + this.getRawItem().getDisplayName() );

            //--------------------------------------------------------------------------------------

                return stack;

            //--------------------------------------------------------------------------------------
            }

            public IBakedModel getCompressedModel() {
            //--------------------------------------------------------------------------------------
                if( null == this.model ) this.model = new BakedModel( this );
            //--------------------------------------------------------------------------------------

                return this.model;

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            public Integer getColor() {
            //--------------------------------------------------------------------------------------

                Integer size = EnumDyeColor.values().length;
                Integer dye  = EnumDyeColor.byDyeDamage( ( 1 + Height ) % size ).getColorValue();

            //--------------------------------------------------------------------------------------
                return ( dye << 8 ) + 0xFF;
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

        }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

        public static Map<Entry , Entry> compressed = new LinkedHashMap<>();

    //==============================================================================================
    // Parser section
    //==============================================================================================

        /*******************************************************************************************
         * @see compressions.Base.Parser
         *******************************************************************************************
         *
         *   Generates a list of Compressed.Entry instances from a file
         *
         ******************************************************************************************/

        public static class Parser extends Base.Parser {

        //==========================================================================================

            /***************************************************************************************
             *
             *   Generated a list of Compressed.Entry instances from an entries and settings files
             *
             ***************************************************************************************
             *
             *   - The generated entries correspond to a single item in game
             *
             ***************************************************************************************
             *
             *   @param entries  a file
             *   @param settings a file
             *
             *   @return a list of filled Compressed.Entry instances
             *
             **************************************************************************************/

            public static Set<Entry> Parse( File entries , File settings ) {
            //══════════════════════════════════════════════════════════════════════════════════════
            // Load default stuff (From the settings file)
            //══════════════════════════════════════════════════════════════════════════════════════

                Configuration config = new Configuration( settings );

            //--------------------------------------------------------------------------------------
                config.load();
            //--------------------------------------------------------------------------------------

                Integer defWidth = config.getInt( "Width" , "General" , 9 , 2 , 9 ,
                        "Default amount of items per pack" );

                Integer defHeight = config.getInt( "Height" , "General" , 3 , 1 , 8 ,
                        "Default number of levels of compression to have" );

            //--------------------------------------------------------------------------------------
                config.save();
            //══════════════════════════════════════════════════════════════════════════════════════
            // Load default compressed entries groups (From the entries file)
            //══════════════════════════════════════════════════════════════════════════════════════

                Set<Entry> global = Parse( entries , Entry.class );

            //══════════════════════════════════════════════════════════════════════════════════════
            // Generate all of the compressed entries from the default entry groups
            //══════════════════════════════════════════════════════════════════════════════════════

                Set<Entry> allEntries = new LinkedHashSet<>();

            //--------------------------------------------------------------------------------------
                for( Entry entry : global ) {
            //--------------------------------------------------------------------------------------

                    if( null == entry.Width  ) entry.Width  = defWidth;
                    if( null == entry.Height ) entry.Height = defHeight;

                //----------------------------------------------------------------------------------
                    for( ItemStack stack : entry.related() ) {for(int i = 0; i < entry.Height; i++){
                //----------------------------------------------------------------------------------

                        Entry single = new Entry( stack );

                        single.Height = i + 1;
                        single.Width = entry.Width;

                        allEntries.add( single );

            //--------------------------------------------------------------------------------------
                } } } return allEntries;
            //══════════════════════════════════════════════════════════════════════════════════════
            }

        //==========================================================================================

        }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

        static /* Load all compressed entries */ {
        //------------------------------------------------------------------------------------------
            String root = Base.root + "/config/" + Base.modId;
        //------------------------------------------------------------------------------------------

            File entriesFile  = Paths.get( root + "/entries.cfg"  ).toFile();
            File settingsFile = Paths.get( root + "/settings.cfg" ).toFile();

        //------------------------------------------------------------------------------------------
            Set<Entry> container = Parser.Parse( entriesFile , settingsFile );
        //------------------------------------------------------------------------------------------

            for( Entry entry : container ) compressed.put( entry , entry );
            for( Entry entry : container ) entries.put   ( entry , entry );

        //------------------------------------------------------------------------------------------
        }

    //==============================================================================================
    // Blocks section
    //==============================================================================================

        /*******************************************************************************************
         * @see compressions.Base.Block
         *******************************************************************************************
         *
         *   A compressed block in the world
         *
         ******************************************************************************************/

        public static class Block extends Base.Block {

        //==========================================================================================

            public Block( Material materialIn , String name ) {
            //--------------------------------------------------------------------------------------
                super( materialIn );
            //--------------------------------------------------------------------------------------

                this.setRegistryName( Base.modId , name );

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override @Nullable public net.minecraft.tileentity.TileEntity createTileEntity(
            //--------------------------------------------------------------------------------------
                    World world ,
                    IBlockState state
            //--------------------------------------------------------------------------------------
            ) {
            //--------------------------------------------------------------------------------------
                return new TileEntity();
            //--------------------------------------------------------------------------------------
            }

            @Override public void onBlockPlacedBy(
            //--------------------------------------------------------------------------------------
                    World worldIn,
                    BlockPos pos,
                    IBlockState state,
                    EntityLivingBase placer,
                    ItemStack stack
            //--------------------------------------------------------------------------------------
            ) { Entry key = new Entry( stack.getTagCompound() );
            //--------------------------------------------------------------------------------------

                Entry entry = compressed.getOrDefault( key , key );

                worldIn.getTileEntity( pos ).getTileData().setTag( "Entry" , entry.toTag() );

            //--------------------------------------------------------------------------------------

                placed.put( pos , entry );

            //--------------------------------------------------------------------------------------
            }

            @Override public void breakBlock( World world , BlockPos pos , IBlockState state ) {
            //--------------------------------------------------------------------------------------

                placed.replace( pos , null );

            //--------------------------------------------------------------------------------------
                super.breakBlock( world, pos, state);
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

        }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

        public static class TileEntity extends Base.TileEntity {

        //==========================================================================================

        }

        public static Map<BlockPos , Entry> placed = new HashMap<>();

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

        /*******************************************************************************************
         *
         *   An instance of a dynamic compressed block (Represents all compressed blocks)
         *
         ******************************************************************************************/

        public static Block BLOCK = new Block( Material.WOOD , ID );

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

        @SubscribeEvent public static void RegBlocks(Register<net.minecraft.block.Block> event){
        //------------------------------------------------------------------------------------------

            event.getRegistry().register( BLOCK );

        //------------------------------------------------------------------------------------------
        }

    //==============================================================================================
    // Items section
    //==============================================================================================

        /*******************************************************************************************
         * @see compressions.Base.ItemBlock
         *******************************************************************************************
         *
         *   A compressed item that can be placed as a BLOCK in the world
         *
         ******************************************************************************************/

        public static class ItemBlock extends Base.ItemBlock {

        //==========================================================================================

            public ItemBlock( Block block , String name ) {
            //--------------------------------------------------------------------------------------
                super( block );
            //--------------------------------------------------------------------------------------

                this.setRegistryName( Base.modId , name );

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public void getSubItems(CreativeTabs tab , NonNullList<ItemStack> items) {
            //--------------------------------------------------------------------------------------

                Boolean search = tab.equals( CreativeTabs.SEARCH );
                Boolean misc   = tab.equals( CreativeTabs.MISC   );

            //--------------------------------------------------------------------------------------
                if( !search && !misc ) return;
            //--------------------------------------------------------------------------------------

                for( Entry entry : compressed.values() ) items.add( entry.getCompressed() );

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

        }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

        /*******************************************************************************************
         *
         *   An instance of a dynamic compressed item (Represents all compressed items that can be
         *   placed as a block in the world)
         *
         ******************************************************************************************/

        public static ItemBlock ITEMBLOCK = new ItemBlock( BLOCK , ID );

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

        @SubscribeEvent public static void RegItems( Register<net.minecraft.item.Item> event ) {
        //------------------------------------------------------------------------------------------

            event.getRegistry().register( ITEMBLOCK );

        //------------------------------------------------------------------------------------------
        }

    //==============================================================================================
    // Model section
    //==============================================================================================

        public static class Overrides extends Base.Overrides {

        //==========================================================================================

            @Override public IBakedModel handleItemState(
            //--------------------------------------------------------------------------------------
                  IBakedModel model
                , ItemStack   stack
                , World       world
                , EntityLivingBase entity
            //--------------------------------------------------------------------------------------
            ) {
            //--------------------------------------------------------------------------------------
                return new Entry( stack.getTagCompound() ).getCompressedModel();
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

        }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

        public static class BakedModel extends Base.BakedModel {

        //==========================================================================================

            BakedModel( Entry parent ) {
            //--------------------------------------------------------------------------------------
                if( null == this.overrides ) this.overrides = new Overrides();
            //--------------------------------------------------------------------------------------

                this.parent = parent;

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public List<BakedQuad> getQuads(
            //--------------------------------------------------------------------------------------
                    @Nullable IBlockState state ,
                    @Nullable EnumFacing  side  ,
                              long        rand
            //--------------------------------------------------------------------------------------
            ) {
            //--------------------------------------------------------------------------------------

                if( null != side       ) return new ArrayList<>();
                if( null == parent     ) return new ArrayList<>();
                if( null == parent.Mod ) return new ArrayList<>();

            //--------------------------------------------------------------------------------------
                Entry entry = (Entry) parent;
            //--------------------------------------------------------------------------------------

                if( TransformType.GUI == transform ) Renderer.renderGUI( entry , transform );
                if( TransformType.GUI != transform ) Renderer.renderNBT( entry , transform );

            //--------------------------------------------------------------------------------------
                transform = null; return new ArrayList<>();
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

        }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

        public static class Model extends Base.Model {

        //==========================================================================================

            @Override public IBakedModel bake(
            //--------------------------------------------------------------------------------------
                      IModelState  state
                    , VertexFormat format
                    , Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter
            //--------------------------------------------------------------------------------------
            ) {
            //--------------------------------------------------------------------------------------
                return new BakedModel( null );
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

        }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

        public static class CustomModelLoader extends Base.CustomModelLoader {

        //==========================================================================================

            @Override public boolean accepts( ResourceLocation modelLocation ) {
            //--------------------------------------------------------------------------------------

                return modelLocation.getResourcePath().toLowerCase().equals( ID ) &&
                       super.accepts( modelLocation );

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public IModel loadModel(ResourceLocation modelLocation) throws Exception {
            //--------------------------------------------------------------------------------------

                return new Model();

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

        }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

        @SubscribeEvent public static void RegModels( ModelRegistryEvent event ) { try {
        //------------------------------------------------------------------------------------------

            ModelLoaderRegistry.registerLoader( new CustomModelLoader() );

        //------------------------------------------------------------------------------------------

            ResourceLocation itemRL  = ITEMBLOCK.getRegistryName();
            ResourceLocation blockRL = BLOCK.getRegistryName();

            ModelResourceLocation itemMRL  = new ModelResourceLocation( itemRL  , "inventory" );
            ModelResourceLocation blockMRL = new ModelResourceLocation( blockRL , "inventory" );

        //------------------------------------------------------------------------------------------

            ModelLoader.setCustomModelResourceLocation( ITEMBLOCK , 0 , itemMRL  );
            ModelLoader.setCustomModelResourceLocation( ITEMBLOCK , 0 , blockMRL );

        //------------------------------------------------------------------------------------------
        } catch( Exception ex ) { ex.printStackTrace(); } }

        @SubscribeEvent public static void onTextureStitch( TextureStitchEvent.Pre event ) {
        //------------------------------------------------------------------------------------------

            event.getMap().registerSprite( new ResourceLocation( Base.modId, "blocks/side" ) );
            event.getMap().registerSprite( new ResourceLocation( Base.modId, "blocks/frame" ) );
            event.getMap().registerSprite( new ResourceLocation( Base.modId, "blocks/frame2" ) );

        /*    event.getMap().registerSprite( new ResourceLocation( Base.modId + "_textures"
                                                               , "side" ) );

            event.getMap().registerSprite( new ResourceLocation( Base.modId + "_textures"
                                                               , "frame" ) );

            event.getMap().registerSprite( new ResourceLocation( Base.modId + "_textures"
                                                               , "frame2" ) );//*/

        //------------------------------------------------------------------------------------------
        }

    //==============================================================================================
    // Rendering section
    //==============================================================================================

        public static class Renderer extends Base.Renderer<TileEntity> {

        //==========================================================================================
            public static Float d = 0.0001f;
        //==========================================================================================

            public static Map<Integer , Integer> frames = new LinkedHashMap<>();
            public static Map<Integer , Integer> gui = new LinkedHashMap<>();

        //==========================================================================================

            public static void renderSide( EnumFacing side , String texture , int color ) {
            //--------------------------------------------------------------------------------------

                Float R = ( ( color >> 24 ) & 255 ) * 1.0f / 255.0f;
                Float G = ( ( color >> 16 ) & 255 ) * 1.0f / 255.0f;
                Float B = ( ( color >>  8 ) & 255 ) * 1.0f / 255.0f;
                Float A = ( ( color >>  0 ) & 255 ) * 1.0f / 255.0f;

            //--------------------------------------------------------------------------------------

                ResourceLocation rl = new ResourceLocation( Base.modId , "blocks/" + texture );
            //    ResourceLocation rl = new ResourceLocation( Base.modId + "_textures" , texture );
                TextureAtlasSprite sprite = Minecraft.getMinecraft()
                                                     .getTextureMapBlocks()
                                                     .getAtlasSprite( rl.toString() );

            //--------------------------------------------------------------------------------------

                Float[] dx = new Float[] { -0.5f - d , -0.5f - d , +0.5f + d , +0.5f + d };
                Float[] du = new Float[] { sprite.getMinU()
                                         , sprite.getMinU()
                                         , sprite.getMaxU()
                                         , sprite.getMaxU() };

                Float[] dz = new Float[] { -0.5f - d , +0.5f + d , +0.5f + d , -0.5f - d };
                Float[] dv = new Float[] { sprite.getMinV()
                                         , sprite.getMaxV()
                                         , sprite.getMaxV()
                                         , sprite.getMinV() };

            //--------------------------------------------------------------------------------------

                GlStateManager.glBegin( GL11.GL_QUADS );

            //--------------------------------------------------------------------------------------
                for( int i = 0; i < 4; i++ ) { switch( side ) {
            //--------------------------------------------------------------------------------------

                    case UP: GlStateManager.glNormal3f( 0.0f , 1.0f , 0.0f );
                             GlStateManager.glTexCoord2f( du[i] , dv[i] );
                             GlStateManager.color( R , G , B , A );
                             GlStateManager.glVertex3f( dx[i] , 0.5f + d , dz[i] );

                //----------------------------------------------------------------------------------
                     break;
                //----------------------------------------------------------------------------------

                    case DOWN: GlStateManager.glNormal3f( 0.0f , -1.0f , 0.0f );
                               GlStateManager.glTexCoord2f( du[i] , dv[i] );
                               GlStateManager.color( R , G , B , A );
                               GlStateManager.glVertex3f( dx[i] , -0.5f - d , -dz[i] );

                //----------------------------------------------------------------------------------
                     break;
                //----------------------------------------------------------------------------------

                    case NORTH: GlStateManager.glNormal3f( 0.0f , 0.0f , +1.0f );
                                GlStateManager.glTexCoord2f( du[i] , dv[i] );
                                GlStateManager.color( R , G , B , A );
                                GlStateManager.glVertex3f( dx[i] , dz[i] , -0.5f - d );

                //----------------------------------------------------------------------------------
                     break;
                //----------------------------------------------------------------------------------

                    case SOUTH: GlStateManager.glNormal3f( 0.0f , 0.0f , -1.0f );
                                GlStateManager.glTexCoord2f( du[i] , dv[i] );
                                GlStateManager.color( R , G , B , A );
                                GlStateManager.glVertex3f( dx[i] , -dz[i] , +0.5f + d );

                //----------------------------------------------------------------------------------
                     break;
                //----------------------------------------------------------------------------------

                    case WEST: GlStateManager.glNormal3f( 1.0f , 0.0f , 0.0f );
                               GlStateManager.glTexCoord2f( du[i] , dv[i] );
                               GlStateManager.color( R , G , B , A );
                               GlStateManager.glVertex3f( 0.5f + d , dz[i] , dx[i] );

                //----------------------------------------------------------------------------------
                     break;
                //----------------------------------------------------------------------------------

                    case EAST: GlStateManager.glNormal3f( -1.0f , 0.0f , 0.0f );
                               GlStateManager.glTexCoord2f( du[i] , dv[i] );
                               GlStateManager.color( R , G , B , A );
                               GlStateManager.glVertex3f( -0.5f - d , -dz[i] , dx[i] );

            //--------------------------------------------------------------------------------------
                } }
            //--------------------------------------------------------------------------------------

                GlStateManager.glEnd();

            //--------------------------------------------------------------------------------------
            }

            public static void renderFrame( @Nullable Entry entry ) {
            //--------------------------------------------------------------------------------------
                bindDefTex();
            //--------------------------------------------------------------------------------------

                Integer display = null;

            //--------------------------------------------------------------------------------------
                if( null != entry ) {
            //--------------------------------------------------------------------------------------

                    display = frames.getOrDefault( entry.Height , null );

                    if( null != display ) GlStateManager.callList( display );
                    if( null != display ) return;

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------

                if( null != entry ) display = GlStateManager.glGenLists( 1 );

            //--------------------------------------------------------------------------------------
                if( null != entry ) GlStateManager.glNewList( display , GL11.GL_COMPILE );
            //--------------------------------------------------------------------------------------

                Integer color = null != entry ? entry.getColor() : 0xADAD00FF;

                Float[][] push = new Float[][] { { 0.0f , -d + 1.0f , 0.0f }    // DOWN
                                               , { 0.0f , +d - 1.0f , 0.0f }    // UP
                                               , { 0.0f , 0.0f , -d + 1.0f }    // NORTH
                                               , { 0.0f , 0.0f , +d - 1.0f }    // SOUTH
                                               , { +d - 1.0f , 0.0f , 0.0f }    // WEST
                                               , { -d + 1.0f , 0.0f , 0.0f } }; // EAST

            //--------------------------------------------------------------------------------------
                for( int i = 0; i < EnumFacing.values().length; i++ ) {
            //--------------------------------------------------------------------------------------

                    EnumFacing side = EnumFacing.values()[i];

                    Float[] translate = push[side.getIndex()];

                //----------------------------------------------------------------------------------

                    renderSide( side , "frame" , color );

                //----------------------------------------------------------------------------------

                    GlStateManager.translate( +translate[0] , +translate[1] , +translate[2] );

                    renderSide( side , "side"  , 0xFFFFFFFF );
                    renderSide( side , "frame" , color );

                    GlStateManager.translate( -translate[0] , -translate[1] , -translate[2] );

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------

                GlStateManager.color( 1.0f , 1.0f , 1.0f , 1.0f );

            //--------------------------------------------------------------------------------------
                if( null != entry ) GlStateManager.glEndList();
            //--------------------------------------------------------------------------------------

                if( null != entry ) GlStateManager.callList( display );
                if( null != entry ) frames.put( entry.Height , display );

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            public static void renderGUI( @Nullable Entry entry , TransformType transform ) {
            //--------------------------------------------------------------------------------------
                Start(); bindDefTex();
            //--------------------------------------------------------------------------------------

                Integer display = null;

            //--------------------------------------------------------------------------------------
                side: { if( null != entry ) {
            //--------------------------------------------------------------------------------------

                        display = gui.getOrDefault( entry.Height , null );

                        if( null != display ) GlStateManager.callList( display );
                        if( null != display ) break side;

                //----------------------------------------------------------------------------------
                    }
                //----------------------------------------------------------------------------------

                    if( null != entry ) display = GlStateManager.glGenLists( 1 );

                //----------------------------------------------------------------------------------
                    if( null != entry ) GlStateManager.glNewList( display , GL11.GL_COMPILE );
                //----------------------------------------------------------------------------------

                    Integer color = null != entry ? entry.getColor() : 0xADAD00FF;

                //----------------------------------------------------------------------------------

                    GlStateManager.translate( +0.5f , +0.5f , +0.5f );

                //----------------------------------------------------------------------------------

                    GlStateManager.translate( 0.0f , 0.0f , -1.0f );

                    renderSide( EnumFacing.SOUTH , "side"   , 0xFFFFFFFF );
                    renderSide( EnumFacing.SOUTH , "frame2" , color );

                    GlStateManager.translate( 0.0f , 0.0f , +1.0f );

                //----------------------------------------------------------------------------------

                    GlStateManager.color( 1.0f , 1.0f , 1.0f , 1.0f );

                //----------------------------------------------------------------------------------
                    if( null != entry ) GlStateManager.glEndList();
                //----------------------------------------------------------------------------------

                    if( null != entry ) GlStateManager.callList( display );
                    if( null != entry ) gui.put( entry.Height , display );

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------

                ForgeHooksClient.handleCameraTransforms( entry.getRawModel() , transform , false );

            //--------------------------------------------------------------------------------------
                Float scale = 0.7f;
            //--------------------------------------------------------------------------------------

                GlStateManager.scale( scale , scale , scale );

                renderRaw( entry );

                GlStateManager.scale( 1.0f / scale , 1.0f / scale , 1.0f / scale );

            //--------------------------------------------------------------------------------------

                GlStateManager.translate( -0.5f , -0.5f , -0.5f );

            //--------------------------------------------------------------------------------------
                End( false );
            //--------------------------------------------------------------------------------------
            }

            public static void renderNBT( @Nullable Entry entry , TransformType transform ) {
            //--------------------------------------------------------------------------------------
                Start();
            //--------------------------------------------------------------------------------------

                Float   scale = 0.7f;
                Integer color = null != entry ? entry.getColor() : 0xADAD00FF;

                GlStateManager.translate( +0.5f , +0.5f , +0.5f );

            //--------------------------------------------------------------------------------------

                ForgeHooksClient.handleCameraTransforms( entry.getRawModel() , transform , false );

            //--------------------------------------------------------------------------------------

                GlStateManager.translate(  0.0f ,  0.0f , +0.5f );

                renderSide( EnumFacing.NORTH , "side"   , 0xFFFFFFFF );
                renderSide( EnumFacing.NORTH , "frame2" , color );

                GlStateManager.translate( 0.0f , 0.0f , -0.5f );
                GlStateManager.translate( 0.0f , 0.0f , -0.5f );

                renderSide( EnumFacing.SOUTH , "side"   , 0xFFFFFFFF );
                renderSide( EnumFacing.SOUTH , "frame2" , color );

                GlStateManager.translate(  0.0f ,  0.0f , +0.5f );

            //--------------------------------------------------------------------------------------

                GlStateManager.scale( scale , scale , scale );

                renderRaw( entry );

                GlStateManager.scale( 1.0f / scale , 1.0f / scale , 1.0f / scale );

            //--------------------------------------------------------------------------------------

                GlStateManager.translate( -0.5f , -0.5f , -0.5f );

            //--------------------------------------------------------------------------------------
                End( false );
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public void render(
            //--------------------------------------------------------------------------------------
                    TileEntity te,
                    double x     ,
                    double y     ,
                    double z     ,
                    float  tick  ,
                    int    stage ,
                    float  alpha
            //--------------------------------------------------------------------------------------
            ) {  /*org.lwjgl.input.Mouse.setGrabbed( false ); //*/
            //--------------------------------------------------------------------------------------

                Entry key = new Entry( te.getTileData().getCompoundTag( "Entry" ) );

                Entry entry = compressed.getOrDefault( key , null );

            //--------------------------------------------------------------------------------------
                if( null == entry ) {
            //--------------------------------------------------------------------------------------

                    entry = compressed.getOrDefault(placed.getOrDefault(te.getPos() , null) , null);

                    if( null != entry ) te.getTileData().setTag( "Entry" , entry.toTag() );
                    if( null == entry ) entry = key;

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------

                GlStateManager.enableBlend();
                GlStateManager.blendFunc( GlStateManager.SourceFactor.SRC_ALPHA,
                                          GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA );

            //--------------------------------------------------------------------------------------
                Start();
            //--------------------------------------------------------------------------------------

                GlStateManager.translate( x + 0.5f , y + 0.5f , z + 0.5f );

                renderFrame( entry );
                renderRaw( entry );

            //--------------------------------------------------------------------------------------
                End( true );
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

        }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

        @SubscribeEvent public static void RegRenderers(Register<net.minecraft.block.Block> event){
        //------------------------------------------------------------------------------------------

            ClientRegistry.registerTileEntity( TileEntity.class , ID , new Renderer() );

        //------------------------------------------------------------------------------------------
        }

    //==============================================================================================
    // Recipes section
    //==============================================================================================

        public static class ShapelessRecipe extends Base.ShapelessRecipe {

        //==========================================================================================

            public ShapelessRecipe () {
            //--------------------------------------------------------------------------------------
                super( "Compression" , ItemStack.EMPTY , NonNullList.create() );
            //--------------------------------------------------------------------------------------

                this.setRegistryName( Base.modId , ID );

            //--------------------------------------------------------------------------------------
            }

            public ShapelessRecipe(
            //--------------------------------------------------------------------------------------
                  String group
                , ItemStack output
                , NonNullList<Ingredient> ingredients
            //--------------------------------------------------------------------------------------
            ) { super( group , output , ingredients );
            //--------------------------------------------------------------------------------------

                this.setRegistryName( Base.modId , ID );

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Nullable public Entry getGridEntry( InventoryCrafting grid ) {
            //--------------------------------------------------------------------------------------

                ItemStack stack = getGridStack( grid );

            //--------------------------------------------------------------------------------------
                if( null == stack ) return null;
            //--------------------------------------------------------------------------------------

                if( !( stack.getItem() instanceof ItemBlock ) ) return new Entry( stack );

            //--------------------------------------------------------------------------------------
                if( !stack.hasTagCompound() ) return null;
            //--------------------------------------------------------------------------------------

                return compressed.getOrDefault( new Entry( stack.getTagCompound() ) , null );

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public boolean matches( InventoryCrafting grid , World world ) {
            //--------------------------------------------------------------------------------------

                ItemStack stack = getGridStack( grid );
                Entry     entry = getGridEntry( grid );

            //--------------------------------------------------------------------------------------

                if( null == stack ) return false;
                if( null == entry ) return false;

            //--------------------------------------------------------------------------------------
                List<Entry> related = entry.related( compressed.values() );
                            related.sort( Comparator.comparing( s -> s.Height ) );
            //--------------------------------------------------------------------------------------

                if( related.isEmpty() ) return false;

            //--------------------------------------------------------------------------------------
                entry = related.get( 0 );
            //--------------------------------------------------------------------------------------

                Boolean raw = !( stack.getItem() instanceof ItemBlock );

                if( !raw && entry.Height >= related.size()  ) return false;
                if(  raw && stack.getCount() != entry.Width ) return false;
                if( !raw && stack.getCount() != entry.Width && stack.getCount() != 1 ) return false;

            //--------------------------------------------------------------------------------------
                return true;
            //--------------------------------------------------------------------------------------
            }

            @Override public ItemStack getCraftingResult( InventoryCrafting grid ) {
            //--------------------------------------------------------------------------------------

                ItemStack stack = getGridStack( grid );
                Entry     entry = getGridEntry( grid );

            //--------------------------------------------------------------------------------------

                List<Entry> related = entry.related( compressed.values() );
                            related.sort( Comparator.comparing( s -> s.Height ) );

            //--------------------------------------------------------------------------------------

                Integer width = related.get( 0 ).Width;
                Integer count = stack.getCount();
                Boolean raw   = !( stack.getItem() instanceof ItemBlock );

            //--------------------------------------------------------------------------------------

                if( raw && width == count ) return related.get( 0 ).getCompressed();

                ItemStack out = ItemStack.EMPTY;

            //--------------------------------------------------------------------------------------
                if( 1 == count ) {
            //--------------------------------------------------------------------------------------

                    if( entry.Height > related.size() ) return ItemStack.EMPTY;

                    if( 1 == entry.Height ) out = entry.getRawItem();
                    if( 1 <  entry.Height ) out = related.get( entry.Height - 2 ).getCompressed();

                    out.setCount( width );

            //--------------------------------------------------------------------------------------
                } else if( width == count )  {
            //--------------------------------------------------------------------------------------

                    if( entry.Height >= related.size() ) return ItemStack.EMPTY;

                    return related.get( entry.Height ).getCompressed();

            //--------------------------------------------------------------------------------------
                } return out;
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

        }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

        public static ShapelessRecipe SHAPELESSRECIPE = new ShapelessRecipe();

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

        @SubscribeEvent public static void Register( Register<IRecipe> event ) {
        //------------------------------------------------------------------------------------------

            event.getRegistry().register( SHAPELESSRECIPE );

        //------------------------------------------------------------------------------------------
        }

    //==============================================================================================

    }

//==================================================================================================
