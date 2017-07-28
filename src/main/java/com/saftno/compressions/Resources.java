//==================================================================================

    package com.saftno.compressions;

//==================================================================================

    import net.minecraft.client.Minecraft;
    import net.minecraft.client.resources.ResourcePackRepository;
    import net.minecraft.client.resources.ResourcePackRepository.Entry;
    import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
    import org.apache.commons.io.FileUtils;

//==================================================================================

    import java.io.File;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.io.OutputStream;
    import java.nio.file.FileSystem;
    import java.nio.file.FileSystems;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.nio.file.Files;
    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.List;
    import java.util.function.Function;
    import java.util.stream.Collectors;

//==================================================================================
    @SuppressWarnings( { "WeakerAccess" , "unused" } )
//==================================================================================

    public class Resources {

    //==============================================================================

        public static Path tmpPath;
        public static Path modPath;

        public static FileSystem tmp;
        public static FileSystem mod;

        public static Boolean tmpEmpty;

    //==============================================================================

        public static void Append( String data , Path path ) { try {
        //--------------------------------------------------------------------------
            if( Files.exists( path ) ) {
        //--------------------------------------------------------------------------

                data += Files.newBufferedReader( path )
                             .lines()
                             .collect( Collectors.joining() );

        //--------------------------------------------------------------------------
            }
        //--------------------------------------------------------------------------

            Resources.Write( data , path );

        //--------------------------------------------------------------------------
        } catch ( IOException e ) { e.printStackTrace(); }}

        public static void Write ( String data , Path path ) { try {
        //--------------------------------------------------------------------------
            if(  Files.exists( path )  ) Files.delete( path );
            if(null != path.getParent()) Files.createDirectories(path.getParent());
        //--------------------------------------------------------------------------

            OutputStream output = Files.newOutputStream( path );

        //--------------------------------------------------------------------------

            output.write( data.getBytes() );

        //--------------------------------------------------------------------------

            output.flush();
            output.close();

        //--------------------------------------------------------------------------
        } catch ( IOException e ) { e.printStackTrace(); }}

    //==============================================================================

        public static class Initialization {

        //==========================================================================

            public static void Pre( FMLPreInitializationEvent event ) { try {
            //----------------------------------------------------------------------

                String[] files = new File( Base.root + "/mods/" ).list();

            //----------------------------------------------------------------------
                if( null == files ) return;
            //----------------------------------------------------------------------

                ArrayList<String> mods = new ArrayList<>( Arrays.asList( files ) );

                mods.removeIf( file -> !file.contains( Base.name ) );
                mods.add( "" );

            //----------------------------------------------------------------------
                String tmpName = Base.root + "/resourcepacks/" + Base.name + ".zip";
            //----------------------------------------------------------------------

                tmpPath = Paths.get( tmpName );

            //----------------------------------------------------------------------
                String modName = Base.root + "/mods/" + mods.get( 0 );
            //----------------------------------------------------------------------

                if( 1 == mods.size() ) modPath = tmpPath;
                if( 2 == mods.size() ) modPath = Paths.get( modName );

            //----------------------------------------------------------------------

                FileUtils.deleteQuietly( tmpPath.toFile() );
                FileUtils.touch( tmpPath.toFile() );

            //----------------------------------------------------------------------

                byte[] empty = { 80 , 75 , 5 , 6 , 0 , 0 , 0 , 0 , 0 , 0 , 0 ,
                                  0 ,  0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 };

                OutputStream zipStream = new FileOutputStream( tmpPath.toFile() );

                zipStream.write( empty , 0 , 22 );

                zipStream.flush();
                zipStream.close();

            //----------------------------------------------------------------------

                tmp = FileSystems.newFileSystem( tmpPath , null );
                mod = FileSystems.newFileSystem( modPath , null );

            //----------------------------------------------------------------------

                String packData = String.join( "\n" , new String[] {
                //------------------------------------------------------------------
                    "{ 'pack' : { 'pack_format' : 3                " ,
                    "           , 'description' : 'Temporary' } } " ,
                //------------------------------------------------------------------
                } ).replace( "'" , "\"" );

            //----------------------------------------------------------------------
                Path packPath = tmp.getPath( "pack.mcmeta" );
            //----------------------------------------------------------------------

                if( !Files.exists( packPath ) ) Write( packData , packPath );

            //----------------------------------------------------------------------

                if( modPath == tmpPath ) mod.close();
                if( modPath == tmpPath ) mod = null;

            //----------------------------------------------------------------------

                tmp.close();
                tmp = FileSystems.newFileSystem( tmpPath , null );

            //----------------------------------------------------------------------
            } catch ( IOException e ) { e.printStackTrace(); } }

        //==========================================================================

        }

        public static class Generation {

        //==========================================================================

            public static void Flush() { try {
            //----------------------------------------------------------------------
                if( null == tmp ) return;
            //----------------------------------------------------------------------

                tmpEmpty = !Files.exists( tmp.getPath( "assets" ) );

            //----------------------------------------------------------------------

                if( null != mod ) mod.close();
                if( null != tmp ) tmp.close();

                if( tmpEmpty ) mod = null;
                if( tmpEmpty ) tmp = null;

                if( tmpEmpty ) return;

            //----------------------------------------------------------------------

                mod = FileSystems.newFileSystem( modPath , null );
                tmp = FileSystems.newFileSystem( tmpPath , null );

                if( modPath == tmpPath ) mod.close();
                if( modPath == tmpPath ) mod = null;

            //----------------------------------------------------------------------
            } catch ( IOException e ) { e.printStackTrace(); } }

        //==========================================================================

        }

        public static class Registration {

        //==========================================================================

            public static void Packs() { try {
            //----------------------------------------------------------------------

                if( null != mod ) mod.close();
                if( null != tmp ) tmp.close();

                // Don't put tmp.close() near Minecraft.getMinecraft()
                // .refreshResources(), as it refreshes before the file IO
                // finishes, you get a broken pack
                // Also mod might be the same as tmp, so close it first

            //----------------------------------------------------------------------

                ResourcePackRepository repo;

                repo = Minecraft.getMinecraft().getResourcePackRepository();
                repo.updateRepositoryEntriesAll();

            //----------------------------------------------------------------------

                List<Entry> all = new ArrayList<>( repo.getRepositoryEntriesAll() );
                List<Entry> on  = new ArrayList<>( repo.getRepositoryEntries()    );

            //----------------------------------------------------------------------
                Function<Entry, Boolean> our = ( entry ) -> {
                //------------------------------------------------------------------
                    String name = entry.getResourcePackName();

                    return name.contains( Base.name );
                //------------------------------------------------------------------
                };
            //----------------------------------------------------------------------

                Entry pack = null;

                for( Entry entry: on  ) { if( our.apply(entry) ) { pack = entry; } }
                for( Entry entry: all ) { if( our.apply(entry) ) { pack = entry; } }

            //----------------------------------------------------------------------

                if( tmpEmpty ) FileUtils.deleteQuietly( tmpPath.toFile() );

                if(  tmpEmpty &&  on.contains( pack ) ) on.remove( pack );
                if( !tmpEmpty && !on.contains( pack ) ) on.add( pack );

            //----------------------------------------------------------------------

                repo.setRepositories( on );
                repo.updateRepositoryEntriesAll();

                Minecraft.getMinecraft().refreshResources();

            //----------------------------------------------------------------------
            } catch ( IOException e ) { e.printStackTrace(); } }

        //==========================================================================

        }

    //==============================================================================

    }

//==================================================================================
