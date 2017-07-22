//==========================================================================================

    package com.saftno.compressions;

//==========================================================================================

    import net.minecraft.block.Block;
    import net.minecraft.item.Item;
    import net.minecraft.item.crafting.IRecipe;
    import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
    import net.minecraftforge.client.event.ModelRegistryEvent;
    import net.minecraftforge.event.RegistryEvent.Register;
    import net.minecraftforge.fml.common.Mod;
    import net.minecraftforge.fml.common.SidedProxy;
    import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
    import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

//==========================================================================================

    @Mod( modid   = Base.modId   , dependencies = "after:*" ,
          name    = Base.name    , acceptedMinecraftVersions = "[1.12]"  ,
          version = Base.version ) @Mod.EventBusSubscriber public class Base {

    //======================================================================================

        @SidedProxy( serverSide = "com.saftno.compressions.Proxies$Common" ,
                     clientSide = "com.saftno.compressions.Proxies$Client" )

        static Proxies.Common proxy;

    //======================================================================================

        static final String modId   = "compressions";
        static final String name    = "Compressions";
        static final String version = "1.0.0";

    //======================================================================================

        @Mod.Instance( modId ) public static Base instance;

    //======================================================================================

        static String          root = System.getProperty("user.dir");
        static private boolean once = false;

    //======================================================================================
    // Initialization
    //======================================================================================

        @Mod.EventHandler public static void initPre( FMLPreInitializationEvent  event ) {
        //----------------------------------------------------------------------------------

            Logging.Initialization.Pre( event );

        //----------------------------------------------------------------------------------
            Logging.info( name + " is loading" );
        //----------------------------------------------------------------------------------

            Logging.info( name + "Resources - loading" );
            Resources.Initialization.Pre( event );
            Logging.info( name + "Resources - success" );

            Logging.info( name + "Configurations - loading" );
            Configurations.Initialization.Pre( event );
            Logging.info( name + "Configurations - success" );

        //----------------------------------------------------------------------------------
            Logging.info( name + " is loading 2" );
        //----------------------------------------------------------------------------------

            Models.Initialization.Pre( event );
            Languages.Initialization.Pre( event );
            Recipes.Initialization.Pre( event );

        //----------------------------------------------------------------------------------
            Logging.info( name + " is loading 3" );
        //----------------------------------------------------------------------------------

            Blocks.Initialization.Pre( event );

        //----------------------------------------------------------------------------------
            Logging.info( name + " is loading 4" );
        //----------------------------------------------------------------------------------
        }

    //======================================================================================
    // Events
    //======================================================================================

        @SubscribeEvent public static void regBlocks( Register<Block> event ) {
        //----------------------------------------------------------------------------------

            Blocks.Registration.Blocks( event );

        //----------------------------------------------------------------------------------
        }

        @SubscribeEvent public static void regItems( Register<Item> event ) {
        //----------------------------------------------------------------------------------

            Blocks.Registration.Items( event );

        //----------------------------------------------------------------------------------

            Languages.Generation.LANG();

        //----------------------------------------------------------------------------------
            Resources.Generation.Flush();
        //----------------------------------------------------------------------------------

            Recipes.Generation.JSON();
            Models.Generation.Blockstates();

        //----------------------------------------------------------------------------------
            Resources.Generation.Flush();
        //----------------------------------------------------------------------------------
        }

        @SubscribeEvent public static void regRecipes( Register<IRecipe> event ) {
        //----------------------------------------------------------------------------------

            Blocks.Registration.Recipes( event );

        //----------------------------------------------------------------------------------
        }

        @SubscribeEvent public static void regModels( ModelRegistryEvent event ) {
        //----------------------------------------------------------------------------------

            Blocks.Registration.Models( event );

        //----------------------------------------------------------------------------------
        }

        @SubscribeEvent public static void regResourcePacks( InitGuiEvent event ) {
        //----------------------------------------------------------------------------------
            if( once ) return;
        //----------------------------------------------------------------------------------

            Textures.Generation.Blocks();

        //----------------------------------------------------------------------------------

            Resources.Registration.Packs();

        //----------------------------------------------------------------------------------
            once = true; Logging.info( name + " finished loading" );
        //----------------------------------------------------------------------------------
        }

    //======================================================================================

    }

//==========================================================================================

