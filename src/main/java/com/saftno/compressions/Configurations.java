//==================================================================================

    package com.saftno.compressions;

//==================================================================================

    import net.minecraft.item.Item;
    import net.minecraftforge.common.config.Configuration;
    import org.apache.commons.io.IOUtils;
    import org.apache.commons.lang3.StringUtils;

//==================================================================================

    import java.io.File;
    import java.io.IOException;
    import java.io.InputStream;
    import java.nio.file.Files;
    import java.nio.file.Paths;
    import java.util.*;

//==================================================================================
    @SuppressWarnings( { "WeakerAccess" , "unused" } )
//==================================================================================

    public class Configurations {

    //==============================================================================
    // Setup
    //==============================================================================

        public static Configuration main;
        public static Configuration burn;

    //==============================================================================

        public static HashMap<String , Integer> burnTime = new HashMap<>();

    //==============================================================================

        static /* Create files */ { try {
        //--------------------------------------------------------------------------
            String dir = Base.root + "/config/compressions/";
        //--------------------------------------------------------------------------

            Files.createDirectories( Paths.get( dir ) );

        //--------------------------------------------------------------------------

            String fileName = dir + "compressions.cfg";
            String burnName = dir + "burn.cfg";

        //--------------------------------------------------------------------------

            main = new Configuration( new File( fileName ) );
            burn = new Configuration( new File( burnName ) );

        //--------------------------------------------------------------------------
        } catch( IOException ex ) { ex.printStackTrace(); } }

    //==============================================================================

        static /* Setup main file */ {
        //--------------------------------------------------------------------------
            main.load();
        //--------------------------------------------------------------------------

            String line = "\r#" + StringUtils.repeat( "-" , 104 ) + "#\n\n";

        //--------------------------------------------------------------------------
            String header = "\t" + String.join( "\n\t" , new String[] {
        //--------------------------------------------------------------------------

            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                "Add things to have compressions here" ,
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        //--------------------------------------------------------------------------
            } ) + "\n"; String footer = "\t" + String.join( "\n\t" , new String[] {
        //--------------------------------------------------------------------------

            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                "minecraft:stone:2 <- adds a single stone variant" ,
                "minecraft:stone   <- adds all stone variants    " ,
                "minecraft         <- adds all entries from a mod" ,
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        //--------------------------------------------------------------------------
            } ) + "\n"; String footer2 = "\t" + String.join( "\n\t" , new String[] {
        //--------------------------------------------------------------------------

            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                "If you create too much blocks and items use the" +
                " NotEnoughIDs mod to enable more           \n" ,
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

                "\thttps://mods.curse.com/mc-mods/minecraft/235107-notenoughids" ,

            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        //--------------------------------------------------------------------------
            } ) + "\n"; String depth = "\t" + String.join( "\n\t" , new String[] {
        //--------------------------------------------------------------------------

            //~~~~~~~~~~~~~~~~~~~~~~~~~~~
                "Levels of compression" ,
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~

        //--------------------------------------------------------------------------
            } ) + "\n"; String single = "\t" + String.join( "\n\t" , new String[] {
        //--------------------------------------------------------------------------

            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                "If you want to have compressed versions of recipes " +
                "you have to add all the items from the recipe.     " ,
                "For example, when adding                         \n" ,
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

                    "\tminecraft:cobblestone                      \n" ,

            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                "you also have to add                             \n" ,
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

                    "\tminecraft:stone:0                          \n" ,

            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                "if you want the compressed cobblestone to smelt int" +
                "o compressed stone. You also have to add         \n" ,
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

                    "\tminecraft:stick                              " ,
                    "\tminecraft:stone_pickaxe                    \n" ,

            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                "if you want the compressed cobblestone to combine w" +
                "ith compressed sticks to form compressed stone     " ,
                "pickaxes.                                        \n" ,
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

                "\tAnd so on ...                                    " ,

            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        //--------------------------------------------------------------------------
            } ) + "\n"; String related = "\t" + String.join( "\n\t" , new String[] {
        //--------------------------------------------------------------------------

            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                "Adds compressed versions of all the items and blocks " +
                "from all the related recipes. For example, adding  \n" ,
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

                    "\tminecraft:cobblestone                        \n" ,

            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                "here automatically adds                            \n" ,
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

                    "\tminecraft:stone:0                            \n" ,

            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                "as well, so the compressed cobblestone can smelt into" +
                " compressed stone. It also automatically adds      \n" ,
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

                    "\tminecraft:stick                                " ,
                    "\tminecraft:stone_pickaxe                      \n" ,

            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                "so the compressed cobblestone can be combined with co" +
                "mpressed sticks to create compressed stone           " ,
                "pickaxes.                                          \n" ,
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

                "\tAnd so on ...                                      " ,

            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        //--------------------------------------------------------------------------
            } ) + "\n";
        //--------------------------------------------------------------------------

            String[] Single  = new String[] {header, single , footer, footer2};
            String[] Related = new String[] {header, related, footer, footer2};

            main.setCategoryComment( "Depth"  , "\n" + depth );
            main.setCategoryComment( "Single" , "\n" + String.join(line, Single ) );
            main.setCategoryComment( "Related", "\n" + String.join(line, Related) );

        //--------------------------------------------------------------------------

            getDepth();
            getSingleIDs();
            getRelatedIDs();

        //--------------------------------------------------------------------------
            main.save();
        //--------------------------------------------------------------------------
        }

        static /* Setup burn file */ { try {
        //--------------------------------------------------------------------------
            burn.load();
        //--------------------------------------------------------------------------

            String g = "Burn times";
            String m = "minecraft:";

        //--------------------------------------------------------------------------
            HashMap<Item , Integer> defTimes = new LinkedHashMap<>();
        //--------------------------------------------------------------------------

            defTimes.put( Item.getByNameOrId( m + "lava_bucket" ) , 20000 );
            defTimes.put( Item.getByNameOrId( m + "coal_block" )  , 16000 );
            defTimes.put( Item.getByNameOrId( m + "blaze_rod" )   , 2400  );
            defTimes.put( Item.getByNameOrId( m + "coal" )        , 1600 );

            defTimes.put( Item.getByNameOrId( m + "boat" )          , 400 );
            defTimes.put( Item.getByNameOrId( m + "spruce_boat" )   , 400 );
            defTimes.put( Item.getByNameOrId( m + "birch_boat" )    , 400 );
            defTimes.put( Item.getByNameOrId( m + "jungle_boat" )   , 400 );
            defTimes.put( Item.getByNameOrId( m + "acacia_boat" )   , 400 );
            defTimes.put( Item.getByNameOrId( m + "dark_oak_boat" ) , 400 );

            defTimes.put( Item.getByNameOrId( m + "log" )   , 300 );
            defTimes.put( Item.getByNameOrId( m + "log2" )   , 300 );
            defTimes.put( Item.getByNameOrId( m + "planks" ) , 300 );

            defTimes.put( Item.getByNameOrId( m + "wooden_pressure_plate" ) , 300 );

            defTimes.put( Item.getByNameOrId( m + "fence" )          , 300 );
            defTimes.put( Item.getByNameOrId( m + "spruce_fence" )   , 300 );
            defTimes.put( Item.getByNameOrId( m + "birch_fence" )    , 300 );
            defTimes.put( Item.getByNameOrId( m + "jungle_fence" )   , 300 );
            defTimes.put( Item.getByNameOrId( m + "acacia_fence" )   , 300 );
            defTimes.put( Item.getByNameOrId( m + "dark_oak_fence" ) , 300 );

            defTimes.put( Item.getByNameOrId( m + "fence_gate" )          , 300 );
            defTimes.put( Item.getByNameOrId( m + "spruce_fence_gate" )   , 300 );
            defTimes.put( Item.getByNameOrId( m + "birch_fence_gate" )    , 300 );
            defTimes.put( Item.getByNameOrId( m + "jungle_fence_gate" )   , 300 );
            defTimes.put( Item.getByNameOrId( m + "acacia_fence_gate" )   , 300 );
            defTimes.put( Item.getByNameOrId( m + "dark_oak_fence_gate" ) , 300 );

            defTimes.put( Item.getByNameOrId( m + "oak_stairs" )      , 300 );
            defTimes.put( Item.getByNameOrId( m + "spruce_stairs" )   , 300 );
            defTimes.put( Item.getByNameOrId( m + "birch_stairs" )    , 300 );
            defTimes.put( Item.getByNameOrId( m + "jungle_stairs" )   , 300 );
            defTimes.put( Item.getByNameOrId( m + "acacia_stairs" )   , 300 );
            defTimes.put( Item.getByNameOrId( m + "dark_oak_stairs" ) , 300 );

            defTimes.put( Item.getByNameOrId( m + "trapdoor" )             , 300 );
            defTimes.put( Item.getByNameOrId( m + "crafting_table" )       , 300 );
            defTimes.put( Item.getByNameOrId( m + "bookshelf" )            , 300 );
            defTimes.put( Item.getByNameOrId( m + "trapped_chest" )        , 300 );
            defTimes.put( Item.getByNameOrId( m + "daylight_detector" )    , 300 );
            defTimes.put( Item.getByNameOrId( m + "jukebox" )              , 300 );
            defTimes.put( Item.getByNameOrId( m + "noteblock" )            , 300 );
            defTimes.put( Item.getByNameOrId( m + "brown_mushroom_block" ) , 300 );
            defTimes.put( Item.getByNameOrId( m + "red_mushroom_block" )   , 300 );

            defTimes.put( Item.getByNameOrId( m + "banner" ) , 300 );

            defTimes.put( Item.getByNameOrId( m + "bow" )         , 300 );
            defTimes.put( Item.getByNameOrId( m + "fishing_rod" ) , 300 );
            defTimes.put( Item.getByNameOrId( m + "ladder" )      , 300 );

            defTimes.put( Item.getByNameOrId( m + "wooden_pickaxe" ) , 200 );
            defTimes.put( Item.getByNameOrId( m + "wooden_hoe" )     , 200 );
            defTimes.put( Item.getByNameOrId( m + "wooden_shovel" )  , 200 );
            defTimes.put( Item.getByNameOrId( m + "wooden_axe" )     , 200 );
            defTimes.put( Item.getByNameOrId( m + "wooden_sword" )   , 200 );

            defTimes.put( Item.getByNameOrId( m + "sign" ) , 200 );

            defTimes.put( Item.getByNameOrId( m + "wooden_door" )   , 200 );
            defTimes.put( Item.getByNameOrId( m + "spruce_door" )   , 200 );
            defTimes.put( Item.getByNameOrId( m + "birch_door" )    , 200 );
            defTimes.put( Item.getByNameOrId( m + "jungle_door" )   , 200 );
            defTimes.put( Item.getByNameOrId( m + "acacia_door" )   , 200 );
            defTimes.put( Item.getByNameOrId( m + "dark_oak_door" ) , 200 );

            defTimes.put( Item.getByNameOrId( m + "wooden_slab" )        , 150 );

            defTimes.put( Item.getByNameOrId( m + "sapling" )       , 100 );
            defTimes.put( Item.getByNameOrId( m + "bowl" )          , 100 );
            defTimes.put( Item.getByNameOrId( m + "stick" )         , 100 );
            defTimes.put( Item.getByNameOrId( m + "wooden_button" ) , 100 );
            defTimes.put( Item.getByNameOrId( m + "wool" )          , 100 );

            defTimes.put( Item.getByNameOrId( m + "carpet" ) , 67 );

        //--------------------------------------------------------------------------

            InputStream input = Files.newInputStream(burn.getConfigFile().toPath());

            String[] content = IOUtils.toString( input , "utf-8" ).split("\n");

            input.close();

        //--------------------------------------------------------------------------

            HashMap<String , Integer> times = new LinkedHashMap<>();

            List<String> entries = new ArrayList<>( Arrays.asList( content ) );
            entries.removeIf( s -> !s.contains( "=" ) );

        //--------------------------------------------------------------------------
            for( String line : entries ) {
        //--------------------------------------------------------------------------

                String  id   = line.split( "\"" )[1];
                Integer time = Integer.parseInt( line.split( "=" )[1] );

                times.put( id , time );

        //--------------------------------------------------------------------------
            } for( Item item : defTimes.keySet() ) {
        //--------------------------------------------------------------------------

                String  id   = item.getRegistryName().toString();
                Integer def  = defTimes.get( item );

            //----------------------------------------------------------------------
                burn.getInt( id , g , def , 0 , 0 , "" );
            //----------------------------------------------------------------------

                burnTime.put( id , times.get( id ) );

        //--------------------------------------------------------------------------
            } burn.save();
        //--------------------------------------------------------------------------
        } catch( IOException ex ) { ex.printStackTrace(); } }

    //==============================================================================
    // Usage
    //==============================================================================

        public static Integer  getDepth() {
        //--------------------------------------------------------------------------
            main.load();
        //--------------------------------------------------------------------------

            int min = 0;
            int def = 3;
            int max = 8;

        //--------------------------------------------------------------------------

            int data = main.getInt( "V" , "Depth" , def , min , max , "Level" );

        //--------------------------------------------------------------------------
            main.save();
        //--------------------------------------------------------------------------

            return data;

        //--------------------------------------------------------------------------
        }

        public static String[] getSingleIDs() {
        //--------------------------------------------------------------------------
            main.load();
        //--------------------------------------------------------------------------

            String name = "IDs";

        //--------------------------------------------------------------------------

            String[] data = main.getStringList("IDs","Single",new String[]{}, "");

        //--------------------------------------------------------------------------
            main.save();
        //--------------------------------------------------------------------------

            return data;

        //--------------------------------------------------------------------------
        }

        public static String[] getRelatedIDs() {
        //--------------------------------------------------------------------------
            main.load();
        //--------------------------------------------------------------------------

            String name = "IDs";

        //--------------------------------------------------------------------------

            String[] data = main.getStringList("IDs","Related",new String[]{}, "");

        //--------------------------------------------------------------------------
            main.save();
        //--------------------------------------------------------------------------

            return data;

        //--------------------------------------------------------------------------
    }

    //==============================================================================

    }

//==================================================================================

