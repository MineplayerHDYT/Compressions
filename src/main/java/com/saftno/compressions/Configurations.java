//==================================================================================

    package com.saftno.compressions;

//==================================================================================

    import net.minecraftforge.common.config.Configuration;
    import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
    import org.apache.commons.lang3.StringUtils;

//==================================================================================

    import java.io.File;
    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Paths;

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

        static /* Setup burn file */ {
        //--------------------------------------------------------------------------
            burn.load();
        //--------------------------------------------------------------------------

        //--------------------------------------------------------------------------
            burn.save();
        //--------------------------------------------------------------------------
        }

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

