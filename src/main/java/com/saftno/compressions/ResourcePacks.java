//==============================================================================================

    package com.saftno.compressions;

//==============================================================================================

    import com.google.gson.JsonObject;
    import com.google.gson.JsonParser;
    import net.minecraft.client.Minecraft;
    import net.minecraft.client.resources.ResourcePackRepository;
    import net.minecraft.client.resources.ResourcePackRepository.Entry;
    import org.apache.commons.io.FileUtils;
    import org.apache.commons.io.IOUtils;

//==============================================================================================

    import javax.imageio.ImageIO;
    import java.awt.image.BufferedImage;
    import java.io.IOException;
    import java.io.OutputStream;
    import java.nio.file.FileSystem;
    import java.nio.file.FileSystems;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.util.ArrayList;
    import java.util.List;

//==============================================================================================

    public class ResourcePacks {

    //==========================================================================================

        public enum Type { ROOT , RECIPE , LANGUAGE , MODEL , TEXTURE }

    //==========================================================================================

        public static String root      = "/[N]";
        public static String recipes   = "/assets/" + Base.modId + "/recipes/[N].json";
        public static String languages = "/assets/" + Base.modId + "/lang/[N].lang";
        public static String models    = "/assets/" + Base.modId + "/blockstates/[N].json";
        public static String textures  = "/assets/" + Base.modId + "/textures/blocks/[N].png";

    //==========================================================================================

        public static FileSystem resPack;

    //==========================================================================================

        static /* Create storage filesystem */ { try {
        //--------------------------------------------------------------------------------------
            Path path = Paths.get( Base.root + "/resourcepacks/" + Base.name + ".zip" );
        //--------------------------------------------------------------------------------------

            byte[] empty = { 80 , 75 , 5 , 6 , 0 , 0 , 0 , 0 , 0 , 0 , 0 ,
                    0 ,  0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 };

        //--------------------------------------------------------------------------------------

            if( !path.toFile().exists() ) FileUtils.writeByteArrayToFile( path.toFile(), empty);

        //--------------------------------------------------------------------------------------

            resPack = FileSystems.newFileSystem( path , null );

        //--------------------------------------------------------------------------------------
            if( Files.exists( resPack.getPath( "pack.mcmeta" ) ) ) {
        //--------------------------------------------------------------------------------------

                Path   metaPath = resPack.getPath( "pack.mcmeta" );
                String content  = IOUtils.toString( Files.newInputStream( metaPath ) , "utf8" );

                JsonObject pack = new JsonParser().parse( content ).getAsJsonObject()
                                                  .get( "pack" ).getAsJsonObject();

                String version = pack.get( "description" ).getAsString();

                Boolean darker = !pack.has("_comment") || pack.get("_comment").getAsBoolean();

                Boolean newer   = !version.equals( Base.version );
                Boolean texdiff = !darker.equals( Configurations.getSettingsDarker() );

            //----------------------------------------------------------------------------------
                if( newer || texdiff ) {
            //----------------------------------------------------------------------------------

                    resPack.close();

                //------------------------------------------------------------------------------

                    FileUtils.deleteQuietly( path.toFile() );
                    FileUtils.touch( path.toFile() );
                    FileUtils.writeByteArrayToFile( path.toFile(), empty);

                //------------------------------------------------------------------------------

                    resPack = FileSystems.newFileSystem( path , null );

        //--------------------------------------------------------------------------------------
            } }
        //--------------------------------------------------------------------------------------
            String packData = String.join( "\n" , new String[] {
        //--------------------------------------------------------------------------------------
                "{ 'pack' : { 'pack_format' : 3 " ,
                "           , 'description' : '" + Base.version + "' " ,
                "           , '_comment' : '" + Configurations.getSettingsDarker() + "' } } " ,
        //--------------------------------------------------------------------------------------
            } ).replace( "'" , "\"" );
        //--------------------------------------------------------------------------------------

            Write( packData , Type.ROOT , "pack.mcmeta" );

        //--------------------------------------------------------------------------------------
        } catch ( IOException e ) { e.printStackTrace(); } }

    //==========================================================================================
    // Usage
    //==========================================================================================

        public static Path Path (Type type , String name ) {
        //--------------------------------------------------------------------------------------

            String location = "";

        //--------------------------------------------------------------------------------------

            if( Type.ROOT     == type ) location = root;
            if( Type.RECIPE   == type ) location = recipes;
            if( Type.LANGUAGE == type ) location = languages;
            if( Type.MODEL    == type ) location = models;
            if( Type.TEXTURE  == type ) location = textures;

        //--------------------------------------------------------------------------------------

            return resPack.getPath( location.replace( "[N]" , name ) );

        //--------------------------------------------------------------------------------------
        }

    //==========================================================================================

        public static void Write( String  data , Type type , String name ) { try {
        //--------------------------------------------------------------------------------------

            Path path = Path( type , name );

        //--------------------------------------------------------------------------------------
            if( Files.exists( path ) ) Files.deleteIfExists( path );
        //--------------------------------------------------------------------------------------

            if( null != path.getParent() ) Files.createDirectories( path.getParent() );

        //--------------------------------------------------------------------------------------

            OutputStream output = Files.newOutputStream( path );

        //--------------------------------------------------------------------------------------

            output.write( data.getBytes() );

        //--------------------------------------------------------------------------------------

            output.flush();
            output.close();

        //--------------------------------------------------------------------------------------
        } catch ( IOException e ) { e.printStackTrace(); }}

        public static void Write( int[][] data , Type type , String name ) { try {
        //--------------------------------------------------------------------------------------
            if( Type.TEXTURE != type ) return;
        //--------------------------------------------------------------------------------------

            Path path = Path( type , name );

        //--------------------------------------------------------------------------------------
            if( Files.exists( path ) ) return;
        //--------------------------------------------------------------------------------------

            int h = data.length;
            int w = data[0].length;

            BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        //--------------------------------------------------------------------------------------
            for( int y = 0; y < h; y++ ) { for( int x = 0; x < w; x++ ) {
        //--------------------------------------------------------------------------------------

                int R = ( data[y][x] >> 24 ) & 255;
                int G = ( data[y][x] >> 16 ) & 255;
                int B = ( data[y][x] >> 8  ) & 255;
                int A = ( data[y][x]       ) & 255;

                image.setRGB( x , y , ( A << 24 ) | ( R << 16 ) | ( G << 8 ) | ( B ) );

        //--------------------------------------------------------------------------------------
            } }
        //--------------------------------------------------------------------------------------

            if(   Files.exists( path )   ) Files.delete( path );
            if( null != path.getParent() ) Files.createDirectories( path.getParent() );

        //--------------------------------------------------------------------------------------

            OutputStream output = Files.newOutputStream( path );

            ImageIO.write( image , "png" , output );

            output.flush();
            output.close();

        //--------------------------------------------------------------------------------------
        } catch ( IOException e ) { e.printStackTrace(); } }

    //==========================================================================================
    // Setup
    //==========================================================================================

        public static void Register() { try {
        //----------------------------------------------------------------------
            Boolean empty = !Files.exists( resPack.getPath( "assets" ) );
        //----------------------------------------------------------------------

            // Check out IResourcePack

            resPack.close();

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

            Entry       pack     = on.isEmpty() ? all.get( 0 ) : on.get( 0 );
            List<Entry> previous = new ArrayList<>( repo.getRepositoryEntries() );
            Boolean     active   = previous.contains( pack );

            if(  empty &&  active ) previous.remove( pack );
            if( !empty && !active ) previous.add( pack );

        //----------------------------------------------------------------------
            String tmpName = Base.root + "/resourcepacks/" + Base.name + ".zip";
        //----------------------------------------------------------------------

            if( empty ) FileUtils.deleteQuietly( Paths.get( tmpName ).toFile() );

        //----------------------------------------------------------------------
            if( empty && !active ) return;
        //----------------------------------------------------------------------

            repo.setRepositories( previous );
            repo.updateRepositoryEntriesAll();

            Minecraft.getMinecraft().refreshResources();

        //----------------------------------------------------------------------
        } catch ( IOException e ) { e.printStackTrace(); } }

    //==========================================================================================

    }

//==============================================================================================

