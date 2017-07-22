//==========================================================================================

    package com.saftno.compressions;

//==========================================================================================

    import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
    import org.apache.commons.io.FileUtils;
    import org.apache.logging.log4j.Level;
    import org.apache.logging.log4j.LogManager;
    import org.apache.logging.log4j.Logger;

//==========================================================================================

    import java.io.BufferedWriter;
    import java.io.File;
    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.StandardOpenOption;

//==========================================================================================

    class Logging {

    //======================================================================================

        static private File LOGFile;
        static private Logger LOG;

    //======================================================================================

        public static class Initialization {

        //==================================================================================

            static void Pre( FMLPreInitializationEvent event ) {
            //------------------------------------------------------------------------------

                LOGFile = new File( Base.root + "/logs/Compressions.txt" );
                LOG = LogManager.getLogger( Base.modId );

            //------------------------------------------------------------------------------

                Logging.clear();

            //------------------------------------------------------------------------------
            }

        //==================================================================================

        }

    //======================================================================================

        static void trace  (String mss,Object... prm) {LOG.log(Level.TRACE,mss,prm);}
        static void debug  (String mss,Object... prm) {LOG.log(Level.DEBUG,mss,prm);}
        static void info   (String mss,Object... prm) {LOG.log(Level.INFO ,mss,prm);}
        static void warning(String mss,Object... prm) {LOG.log(Level.WARN ,mss,prm);}
        static void error  (String mss,Object... prm) {LOG.log(Level.ERROR,mss,prm);}

    //======================================================================================

        static private void clear() {
        //----------------------------------------------------------------------------------

            FileUtils.deleteQuietly(LOGFile);

        //----------------------------------------------------------------------------------
        }

        static void file( String message ) { try {
        //----------------------------------------------------------------------------------
            FileUtils.touch( LOGFile );
        //----------------------------------------------------------------------------------

            StandardOpenOption open = StandardOpenOption.APPEND;

            BufferedWriter output = Files.newBufferedWriter( LOGFile.toPath() , open );

            output.write( message + "\n" );

            output.flush();
            output.close();

        //----------------------------------------------------------------------------------
        } catch ( IOException e ) { e.printStackTrace(); } }

    //======================================================================================

    }

//==========================================================================================

