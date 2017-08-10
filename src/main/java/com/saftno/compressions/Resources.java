//==================================================================================

    package com.saftno.compressions;

//==================================================================================

    import net.minecraft.client.Minecraft;
    import net.minecraft.client.resources.ResourcePackRepository;
    import net.minecraft.client.resources.ResourcePackRepository.Entry;
    import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
    import net.minecraftforge.common.MinecraftForge;
    import net.minecraftforge.fml.common.Mod;
    import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
    import org.apache.commons.io.FileUtils;

//==================================================================================

    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.io.OutputStream;
    import java.nio.file.FileSystem;
    import java.nio.file.FileSystems;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.nio.file.Files;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.stream.Collectors;

//==================================================================================
    @SuppressWarnings( { "WeakerAccess" , "unused" } ) @Mod.EventBusSubscriber
//==================================================================================

    public class Resources {

    //==============================================================================
    // Setup
    //==============================================================================

        public static Boolean tmpEmpty;

    //==============================================================================

        public static Path storagePath;
        public static FileSystem storage;

    //==============================================================================

        static /* Create storage filesystem */ { try {
        //--------------------------------------------------------------------------
            String tmpName = Base.root + "/resourcepacks/" + Base.name + ".zip";
        //--------------------------------------------------------------------------

            storagePath = Paths.get( tmpName );

        //--------------------------------------------------------------------------

            FileUtils.deleteQuietly( storagePath.toFile() );
            FileUtils.touch( storagePath.toFile() );

        //--------------------------------------------------------------------------

            byte[] empty = { 80 , 75 , 5 , 6 , 0 , 0 , 0 , 0 , 0 , 0 , 0 ,
                              0 ,  0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 };

        //--------------------------------------------------------------------------

            OutputStream zipStream = new FileOutputStream( storagePath.toFile() );

            zipStream.write( empty );

            zipStream.flush();
            zipStream.close();

        //--------------------------------------------------------------------------

            storage = FileSystems.newFileSystem(storagePath, null );

        //--------------------------------------------------------------------------
            String packData = String.join( "\n" , new String[] {
        //--------------------------------------------------------------------------

            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                "{ 'pack' : { 'pack_format' : 3               " ,
                "           , 'description' : 'Temporary' } } " ,
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        //--------------------------------------------------------------------------
            } ).replace( "'" , "\"" );
        //--------------------------------------------------------------------------

            Write( packData , storage.getPath( "pack.mcmeta" ) );

        //--------------------------------------------------------------------------
        } catch ( IOException e ) { e.printStackTrace(); } }

    //==============================================================================
    // Usage
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
        @SubscribeEvent
    //==============================================================================

        public static void regResourcePacks( DrawScreenEvent event ) { try {
        //----------------------------------------------------------------------
            if( !Resources.storage.isOpen() ) return;
            //if( null != Resources.mod ) if( !Resources.mod.isOpen()
            //        ) return;
        //----------------------------------------------------------------------

            if( !Languages.over ) Languages.Register( event );
            if( !Textures.over  ) Textures.Register( event );
            if( !Models.over    ) Models.Register( event );

        //----------------------------------------------------------------------
            Boolean empty = !Files.exists( storage.getPath( "assets" ) );
        //----------------------------------------------------------------------

            //if( null != mod ) mod.close();
            if( null != storage) storage.close();

            // Don't put storage.close() near Minecraft.getMinecraft()
            // .refreshResources() as it refreshes before the file IO
            // finishes and you get a broken pack

        //----------------------------------------------------------------------

            ResourcePackRepository repo;

            repo = Minecraft.getMinecraft().getResourcePackRepository();
            repo.updateRepositoryEntriesAll();

        //----------------------------------------------------------------------

            List<Entry> all = new ArrayList<>( repo.getRepositoryEntriesAll() );
            List<Entry> on  = new ArrayList<>( repo.getRepositoryEntries()    );

            all.removeIf( s -> !s.getResourcePackName().contains( Base.name ) );
            on.removeIf( s -> !s.getResourcePackName().contains( Base.name ) );

        //----------------------------------------------------------------------

            Entry   pack   = on.isEmpty() ? all.get( 0 ) : on.get( 0 );
            Boolean active = on.contains( pack );

            if(  empty &&  active ) on.remove( pack );
            if( !empty && !active ) on.add( pack );

        //----------------------------------------------------------------------
            String tmpName = Base.root + "/resourcepacks/" + Base.name + ".zip";
        //----------------------------------------------------------------------

            if( empty ) FileUtils.deleteQuietly( Paths.get(tmpName).toFile() );

        //----------------------------------------------------------------------
            if( empty && !active ) return;
        //----------------------------------------------------------------------

            repo.setRepositories( on );
            repo.updateRepositoryEntriesAll();

            Minecraft.getMinecraft().refreshResources();

        //----------------------------------------------------------------------
            MinecraftForge.EVENT_BUS.unregister( Resources.class );
        //----------------------------------------------------------------------
        } catch ( IOException e ) { e.printStackTrace(); } }

    //==============================================================================

    //==============================================================================

    }

//==================================================================================
