//==================================================================================

    package com.saftno.compressions;

//==================================================================================

    import net.minecraft.block.Block;
    import net.minecraft.client.Minecraft;
    import net.minecraft.client.renderer.block.model.IBakedModel;
    import net.minecraft.client.renderer.block.model.ModelResourceLocation;
    import net.minecraft.item.Item;
    import net.minecraft.item.ItemStack;
    import net.minecraft.item.crafting.IRecipe;
    import net.minecraft.util.ResourceLocation;
    import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
    import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
    import net.minecraftforge.client.event.ModelBakeEvent;
    import net.minecraftforge.client.event.ModelRegistryEvent;
    import net.minecraftforge.client.model.ModelLoader;
    import net.minecraftforge.event.RegistryEvent.Register;
    import net.minecraftforge.fml.common.Mod;
    import net.minecraftforge.fml.common.SidedProxy;
    import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
    import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

    import java.nio.IntBuffer;

//==================================================================================
    @Mod.EventBusSubscriber
//----------------------------------------------------------------------------------
    @Mod( modid   = Base.modId   , dependencies = "after:*" ,
          name    = Base.name    , acceptedMinecraftVersions = "[1.12]"  ,
          version = Base.version )
//----------------------------------------------------------------------------------
    @SuppressWarnings( { "WeakerAccess" , "CanBeFinal" } )
//==================================================================================

    public class Base {

    //==============================================================================
        @SidedProxy( serverSide = "com.saftno.compressions.Proxies$Common" ,
                     clientSide = "com.saftno.compressions.Proxies$Client" )
    //==============================================================================

        public static Proxies.Common proxy;

    //==============================================================================

        public static final String modId   = "compressions";
        public static final String name    = "Compressions";
        public static final String version = "1.0.0";

    //==============================================================================
        @Mod.Instance( modId )
    //==============================================================================

        public static Base instance;

    //==============================================================================

        public static String  root = System.getProperty("user.dir");
        public static boolean once = false;

    //==============================================================================
        @Mod.EventHandler
    //==============================================================================

        public static void initPre( FMLPreInitializationEvent event ) {
        //--------------------------------------------------------------------------

        //--------------------------------------------------------------------------
            Logging.info( name + " is loading" );
        //--------------------------------------------------------------------------

            Resources.Initialization.Pre( event );
            Configurations.Initialization.Pre( event );

        //--------------------------------------------------------------------------

            Models.Initialization.Pre( event );
            Languages.Initialization.Pre( event );
            Recipes.Initialization.Pre( event );

        //--------------------------------------------------------------------------

            Blocks.Initialization.Pre( event );

        //--------------------------------------------------------------------------
        }

        //==========================================================================
        // Events
        //==========================================================================

            public static IntBuffer forgeEndScreen;

        //==========================================================================
            @SubscribeEvent
        //==========================================================================

            public static void forgeEnd( InitGuiEvent event ) {
            //----------------------------------------------------------------------

                if( null == forgeEndScreen ) forgeEndScreen = Textures.GrabScreen();

            //----------------------------------------------------------------------
            }

        //==========================================================================
            @SubscribeEvent
        //==========================================================================

            public static void regBlocks( Register<Block> event ) {
            //----------------------------------------------------------------------

                Blocks.Registration.Blocks( event );

            //----------------------------------------------------------------------
            }

        //==========================================================================
            @SubscribeEvent
        //==========================================================================

            public static void regItems( Register<Item> event ) {
            //----------------------------------------------------------------------

                Blocks.Registration.Items( event );

            //----------------------------------------------------------------------
            }

        //==========================================================================
            @SubscribeEvent
        //==========================================================================

            public static void regRecipes( Register<IRecipe> event ) {
            //----------------------------------------------------------------------

                forgeEndScreen = Textures.GrabScreen();
                Blocks.Registration.Recipes( event );
                forgeEndScreen = null;

            //----------------------------------------------------------------------

            //----------------------------------------------------------------------
            }

        //==========================================================================
            @SubscribeEvent
        //==========================================================================

            public static void regModels( ModelRegistryEvent event ) {
            //----------------------------------------------------------------------

                Blocks.Registration.Models( event );

            //----------------------------------------------------------------------
            }

            public static ModelBakeEvent bk;

            @SubscribeEvent public static void regtest( ModelBakeEvent event ) {
            //----------------------------------------------------------------------

                bk = event;


            //----------------------------------------------------------------------
            }

        //==========================================================================
            @SubscribeEvent
        //==========================================================================

            public static void regResourcePacks( DrawScreenEvent event ) {
            //----------------------------------------------------------------------
                if( once ) return;
            //----------------------------------------------------------------------

                Languages.Generation.LANG();

                //----------------------------------------------------------------------
                Resources.Generation.Flush();
                //----------------------------------------------------------------------

                Recipes.Generation.JSON();
                Models.Generation.Blockstates();
                Models.Generation.Models();
                Textures.Generation.Blocks();

                //----------------------------------------------------------------------
                Resources.Generation.Flush();
                //----------------------------------------------------------------------



                for( Blocks.Stem block : Blocks.blocks ) {

                    ResourceLocation rLoc = block.getRegistryName();
                    ModelResourceLocation mrLoc = new ModelResourceLocation(rLoc,
                            "inventory");
                    ModelResourceLocation mrLoc2 = new ModelResourceLocation(rLoc,
                            "gui");

                    //----------------------------------------------------------------------

                    Item item = block.getAsItem();

                    ItemStack stack = new ItemStack( item , 1 , 0 );
                    IBakedModel model = Minecraft.getMinecraft().getRenderItem()
                            .getItemModelMesher().getItemModel( stack );

                    String name = model.toString();
                    if( name.contains( "FancyMissingModel" ) ) continue;

                    Minecraft.getMinecraft().getRenderItem()
                            .getItemModelMesher().register( item , 0 , mrLoc );

                    //ModelLoader.setCustomModelResourceLocation( item , 0 ,
                    // mrLoc );
                    //ModelLoader.setCustomModelResourceLocation( item , 0 ,
                    //        mrLoc2 );
                    //IBakedModel mod = bk.getModelManager().getModel(mrLoc);

                    //bk.getModelRegistry().putObject(mrLoc , mod);

                }
                //bk.getModelLoader().setupModelRegistry();

            //----------------------------------------------------------------------

                Resources.Registration.Packs();

            //----------------------------------------------------------------------

                //Textures.Generation.saveAllToFile();

            //----------------------------------------------------------------------
                once = true; Logging.info( name + " finished loading" );
            //----------------------------------------------------------------------
            }

    //==============================================================================

    }

//==================================================================================
