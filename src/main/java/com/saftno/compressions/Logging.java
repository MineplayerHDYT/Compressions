//==================================================================================

    package com.saftno.compressions;

//==================================================================================

    import org.apache.commons.io.FileUtils;
    import org.apache.logging.log4j.Level;
    import org.apache.logging.log4j.LogManager;
    import org.apache.logging.log4j.Logger;

//==================================================================================

    import java.io.BufferedWriter;
    import java.io.File;
    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.StandardOpenOption;

//==================================================================================
    @SuppressWarnings( { "WeakerAccess" , "CanBeFinal" , "unused" } )
//==================================================================================

    public class Logging {

    //==============================================================================
    // Setup
    //==============================================================================

        public static File   LOGF;
        public static Logger LOGM;

    //==============================================================================

        static {
        //----------------------------------------------------------------------

            Logging.LOGM = LogManager.getLogger( Base.modId );
            Logging.LOGF = new File( Base.root + "/logs/Compressions.txt" );

        //----------------------------------------------------------------------

            Logging.clear();

        //----------------------------------------------------------------------
        }

    //==============================================================================
    // Usage
    //==============================================================================

        public static void info( String message , Object... parameters ) {
        //--------------------------------------------------------------------------

            LOGM.log( Level.INFO , message , parameters );

        //--------------------------------------------------------------------------
        }

    //==============================================================================

        public static void clear() {
        //--------------------------------------------------------------------------

            FileUtils.deleteQuietly( LOGF );

        //--------------------------------------------------------------------------
        }

        public static void file( String message ) { try {
        //--------------------------------------------------------------------------
            FileUtils.touch( LOGF );
        //--------------------------------------------------------------------------

            StandardOpenOption open = StandardOpenOption.APPEND;

            BufferedWriter output = Files.newBufferedWriter( LOGF.toPath() , open );

            output.write( message + "\n" );

            output.flush();
            output.close();

        //--------------------------------------------------------------------------
        } catch ( IOException e ) { e.printStackTrace(); } }

    //==============================================================================

    }

//==================================================================================
