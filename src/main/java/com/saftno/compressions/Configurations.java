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

        public static Configuration main;
        public static Configuration burn;

    //==============================================================================

        public static class Initialization {

        //==========================================================================

            public static void Pre( FMLPreInitializationEvent event ) { try {
            //----------------------------------------------------------------------
                String dir = Base.root + "/config/compressions/";
                Files.createDirectories( Paths.get( dir ) );
                String fileName = dir + "compressions.cfg";
            //----------------------------------------------------------------------

                main = new Configuration( new File( fileName ) );

            //----------------------------------------------------------------------
                main.load();
            //----------------------------------------------------------------------

                String line = "\r#" + StringUtils.repeat( "-" , 104 ) + "#\n\n";

                String header = String.join( "\n\t" , new String[] {
                //------------------------------------------------------------------
                    "\n\t" +
                    "Add things to have compressions here \n" ,
                //------------------------------------------------------------------
                } );

                String footer = String.join( "\n\t" , new String[] {
                //------------------------------------------------------------------
                    "\t" +
                    "minecraft:stone:2 <- adds a single stone variant  " ,
                    "minecraft:stone   <- adds all stone variants      " ,
                    "minecraft         <- adds all entries from a mod\n" ,
                //------------------------------------------------------------------
                } );

                String footer2 = String.join( "\n\t" , new String[] {
                //------------------------------------------------------------------
                    "\t" +
                    "If you create too much blocks and items use the NotEnoughIDs" +
                    "mod to enable more                                        \n" ,
                //------------------------------------------------------------------
                    "\thttps://mods.curse.com/mc-mods/minecraft/235107-notenoughi" +
                    "ds                                                        \n" ,
                //------------------------------------------------------------------
                } );

            //----------------------------------------------------------------------

                String depth = String.join( "\n\t" , new String[] {
                //------------------------------------------------------------------
                    "\n\tLevels of compression\n" ,
                //------------------------------------------------------------------
                } );

                String single = String.join( "\n\t" , new String[] {
                //------------------------------------------------------------------
                    "\t" +
                    "If you want to have compressed versions of recipes you have " +
                    "to add all the items from the recipe.                       " ,
                    "For example, when adding                                  \n" ,
                //------------------------------------------------------------------
                    "\tminecraft:cobblestone                                   \n" ,
                //------------------------------------------------------------------
                    "you also have to add                                      \n" ,
                //------------------------------------------------------------------
                    "\tminecraft:stone:0                                       \n" ,
                //------------------------------------------------------------------
                    "if you want the compressed cobblestone to smelt into compres" +
                    "sed stone. You also have to add                           \n" ,
                //------------------------------------------------------------------
                    "\tminecraft:stick                                           " ,
                    "\tminecraft:stone_pickaxe                                 \n" ,
                //------------------------------------------------------------------
                    "if you want the compressed cobblestone to combine with compr" +
                    "essed sticks to form compressed stone                       " ,
                    "pickaxes.                                                 \n" ,
                //------------------------------------------------------------------
                    "\tAnd so on ...                                           \n"
                //------------------------------------------------------------------
                } );

                String related = String.join( "\n\t" , new String[] {
                //------------------------------------------------------------------
                    "\t" +
                    "Adds compressed versions of all the items and blocks from " +
                    "all the related recipes. For example, adding            \n" ,
                //------------------------------------------------------------------
                    "\tminecraft:cobblestone                                 \n" ,
                //------------------------------------------------------------------
                    "here automatically adds                                 \n" ,
                //------------------------------------------------------------------
                    "\tminecraft:stone:0                                     \n" ,
                //------------------------------------------------------------------
                    "as well, so the compressed cobblestone can smelt into comp" +
                    "ressed stone. It also automatically adds                \n" ,
                //------------------------------------------------------------------
                    "\tminecraft:stick                                         " ,
                    "\tminecraft:stone_pickaxe                               \n" ,
                //------------------------------------------------------------------
                    "so the compressed cobblestone can be combined with compres" +
                    "sed sticks to create compressed stone                     " ,
                    "pickaxes.                                               \n" ,
                //------------------------------------------------------------------
                    "\tAnd so on ...                                         \n"
                //------------------------------------------------------------------
                } );

            //----------------------------------------------------------------------

                String[] Single  = new String[] {header, single , footer, footer2};
                String[] Related = new String[] {header, related, footer, footer2};

                main.setCategoryComment( "Depth"   , depth );
                main.setCategoryComment( "Single"  , String.join( line, Single  ) );
                main.setCategoryComment( "Related" , String.join( line, Related ) );

            //----------------------------------------------------------------------
                main.save();
            //----------------------------------------------------------------------

                getDepth();
                getSingleIDs();
                getRelatedIDs();

            //----------------------------------------------------------------------
                String burnName = dir + "burn.cfg";
            //----------------------------------------------------------------------


            //----------------------------------------------------------------------
            } catch( IOException ex ) { ex.printStackTrace(); } }

        //==========================================================================

        }

    //==============================================================================

        public static int getDepth() {
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

    //==============================================================================

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

