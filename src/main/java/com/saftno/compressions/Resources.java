//==========================================================================================

    package com.saftno.compressions;

//==========================================================================================

    import net.minecraft.client.Minecraft;
    import net.minecraft.client.resources.ResourcePackRepository;
    import net.minecraft.client.resources.ResourcePackRepository.Entry;
    import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
    import org.apache.commons.io.FileUtils;

//==========================================================================================

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
    import java.util.stream.Collectors;

//==========================================================================================

    @SuppressWarnings( "WeakerAccess" ) class Resources {

    //======================================================================================

        static Path tmpPath;
        static Path modPath;

        static FileSystem tmp;
        static FileSystem mod;

        static Boolean tmpEmpty;

    //======================================================================================

        static void Append( String data , Path path ) { try {
        //----------------------------------------------------------------------------------
            if( Files.exists( path ) ) {
        //----------------------------------------------------------------------------------

                data += Files.newBufferedReader(path).lines().collect(Collectors.joining());

        //----------------------------------------------------------------------------------
            }
        //----------------------------------------------------------------------------------

            Write( data , path );

        //----------------------------------------------------------------------------------
        } catch ( IOException e ) { e.printStackTrace(); }}

        static void Write( String data , Path path ) { try {
        //----------------------------------------------------------------------------------
            if(   Files.exists( path )   ) Files.delete( path );
            if( null != path.getParent() ) Files.createDirectories( path.getParent() );
        //----------------------------------------------------------------------------------

            OutputStream output = Files.newOutputStream( path );

        //----------------------------------------------------------------------------------

            output.write( data.getBytes() );

        //----------------------------------------------------------------------------------

            output.flush();
            output.close();

        //----------------------------------------------------------------------------------
        } catch ( IOException e ) { e.printStackTrace(); }}

    //======================================================================================

        static class Initialization {

        //==================================================================================

            static void Pre(@SuppressWarnings("unused")FMLPreInitializationEvent event){try{
            //------------------------------------------------------------------------------

                String[] files = new File( Base.root + "/mods/" ).list();

            //------------------------------------------------------------------------------
                if( null == files ) return;
            //------------------------------------------------------------------------------

                ArrayList<String> mods  = new ArrayList<>( Arrays.asList( files ) );

                mods.removeIf( file -> !file.contains( Base.name ) );

            //------------------------------------------------------------------------------

                tmpPath = Paths.get(Base.root + "/resourcepacks/" + Base.name + ".zip");

                if(  mods.isEmpty() ) modPath = tmpPath;
                if( !mods.isEmpty() ) modPath = Paths.get(Base.root+"/mods/"+mods.get(0));

            //------------------------------------------------------------------------------

                FileUtils.deleteQuietly( tmpPath.toFile() );
                FileUtils.touch( tmpPath.toFile() );

            //------------------------------------------------------------------------------

                final byte[] empty = {80 , 75 , 5 , 6 , 0 , 0 , 0 , 0 , 0 , 0 , 0 ,
                                       0 ,  0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 };

                OutputStream zipStream = new FileOutputStream( tmpPath.toFile() );

                zipStream.write( empty , 0 , 22 );

                zipStream.flush();
                zipStream.close();

            //------------------------------------------------------------------------------

                tmp = FileSystems.newFileSystem( tmpPath , null );
                mod = FileSystems.newFileSystem( modPath , null );

            //------------------------------------------------------------------------------

                String data ="{ 'pack': { 'pack_format': 3, 'description': 'Temporary' } }";
                data = data.replace( "'" , "\"" );

                Path meta = tmp.getPath( "pack.mcmeta" );
                if( !Files.exists( meta ) ) Write( data , meta );

            //------------------------------------------------------------------------------

                if( mods.isEmpty() ) mod.close();
                if( mods.isEmpty() ) mod = null;

            //------------------------------------------------------------------------------
            } catch ( IOException e ) { e.printStackTrace(); } }

        //==================================================================================

        }

        static class Generation {

        //==================================================================================

            static void Flush() { try {
            //------------------------------------------------------------------------------
                if( null == tmp ) return;
            //------------------------------------------------------------------------------

                tmpEmpty = !Files.exists( tmp.getPath( Languages.file ) );

            //------------------------------------------------------------------------------

                if( null != mod ) mod.close();
                if( null != tmp ) tmp.close();

                if( tmpEmpty ) mod = null;
                if( tmpEmpty ) tmp = null;

                if( tmpEmpty ) return;

            //------------------------------------------------------------------------------

                mod = FileSystems.newFileSystem( modPath , null );
                tmp = FileSystems.newFileSystem( tmpPath , null );

                if( modPath == tmpPath ) mod.close();
                if( modPath == tmpPath ) mod = null;

            //------------------------------------------------------------------------------
            } catch ( IOException e ) { e.printStackTrace(); } }

        //==================================================================================

        }

        static class Registration {

        //==================================================================================

            static void Packs() { try {
            //------------------------------------------------------------------------------

                if( null != mod ) mod.close();
                if( null != tmp ) tmp.close();

                // Don't put tmp.close() near Minecraft.getMinecraft().refreshResources(),
                // as it refreshes before the file IO finishes, you get a broken pack

            //------------------------------------------------------------------------------

                ResourcePackRepository repo;

                repo = Minecraft.getMinecraft().getResourcePackRepository();
                repo.updateRepositoryEntriesAll();

            //------------------------------------------------------------------------------

                ArrayList<Entry> all = new ArrayList<>( repo.getRepositoryEntriesAll() );
                ArrayList<Entry> on  = new ArrayList<>( repo.getRepositoryEntries()    );

            //------------------------------------------------------------------------------

                Entry Compressions = null;

                for( Entry entry : on ) {
                    if( entry.getResourcePackName().contains( Base.name ) ) {
                        Compressions = entry;
                    }
                }

                for( Entry entry : all ) {
                    if( entry.getResourcePackName().contains( Base.name ) ) {
                        Compressions = entry;
                    }
                }

            //------------------------------------------------------------------------------

                if( tmpEmpty ) FileUtils.deleteQuietly( tmpPath.toFile() );

                if(  tmpEmpty &&  on.contains( Compressions ) ) on.remove( Compressions );
                if( !tmpEmpty && !on.contains( Compressions ) ) on.add( Compressions );

            //------------------------------------------------------------------------------

                repo.setRepositories( on );
                repo.updateRepositoryEntriesAll();

                Minecraft.getMinecraft().refreshResources();

            //------------------------------------------------------------------------------
            } catch ( IOException e ) { e.printStackTrace(); } }

        //==================================================================================

        }

    //======================================================================================

    }

//==========================================================================================

