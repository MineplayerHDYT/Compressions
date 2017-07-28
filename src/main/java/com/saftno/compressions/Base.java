//==================================================================================

    package com.saftno.compressions;

//==================================================================================

    import net.minecraft.block.Block;
    import net.minecraft.item.Item;
    import net.minecraft.item.crafting.IRecipe;
    import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
    import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
    import net.minecraftforge.client.event.ModelRegistryEvent;
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

            Logging.Initialization.Pre( event );

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

                Languages.Generation.LANG();

            //----------------------------------------------------------------------
                Resources.Generation.Flush();
            //----------------------------------------------------------------------

                Recipes.Generation.JSON();
                Models.Generation.Blockstates();

            //----------------------------------------------------------------------
                Resources.Generation.Flush();
            //----------------------------------------------------------------------
            }

        //==========================================================================
            @SubscribeEvent
        //==========================================================================

            public static void regRecipes( Register<IRecipe> event ) {
            //----------------------------------------------------------------------

                Blocks.Registration.Recipes( event );

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

        //==========================================================================
            @SubscribeEvent
        //==========================================================================

            public static void regResourcePacks( DrawScreenEvent event ) {
            //----------------------------------------------------------------------
                if( once ) return;
            //----------------------------------------------------------------------

                Textures.Generation.Blocks();

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
