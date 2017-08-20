//==================================================================================================

    package compressions;

//==================================================================================================

    import com.google.gson.Gson;
    import com.google.gson.JsonElement;
    import com.google.gson.JsonObject;
    import com.google.gson.JsonParser;
    import net.minecraft.creativetab.CreativeTabs;
    import net.minecraft.item.Item;
    import net.minecraft.item.ItemStack;
    import net.minecraft.nbt.JsonToNBT;
    import net.minecraft.nbt.NBTException;
    import net.minecraft.nbt.NBTTagCompound;
    import net.minecraft.util.NonNullList;
    import net.minecraftforge.common.config.Configuration;
    import net.minecraftforge.fml.common.registry.ForgeRegistries;
    import org.apache.commons.io.FileUtils;
    import org.apache.commons.lang3.StringUtils;

//==================================================================================================

    import java.io.File;
    import java.io.IOException;
    import java.nio.charset.Charset;
    import java.nio.file.Files;
    import java.nio.file.Paths;
    import java.util.*;

//==================================================================================================

    public class Configurations {

    //==============================================================================================
    // Structure
    //==============================================================================================

        public static String root = Base.root + "/config/compressions/";

    //==============================================================================================

        public static String Entries  = "entries.cfg";
        public static String Settings = "settings.cfg";

    //==============================================================================================

        public static List<ItemStack> entries = new ArrayList<>();

    //==============================================================================================
    // Setup
    //==============================================================================================

        static /* Set up the config files */ { init: try {
        //------------------------------------------------------------------------------------------
            if( new File( root + Entries ).exists() ) break init;
        //------------------------------------------------------------------------------------------

            Files.copy( Base.class.getResourceAsStream("/" + Entries) , Paths.get(root + Entries) );

        //------------------------------------------------------------------------------------------
        } catch( IOException ex ) { ex.printStackTrace(); } }

        static /* Load the entries */  { try {
        //──────────────────────────────────────────────────────────────────────────────────────────
        // First we get the item groups from the file
        //──────────────────────────────────────────────────────────────────────────────────────────

            final String div = "parsilibnappredi";

        //------------------------------------------------------------------------------------------

            List<String> groups = new ArrayList<>();

        //------------------------------------------------------------------------------------------

            File     file  = new File( root + Entries );
            String[] lines = FileUtils.readFileToString( file , "utf8" ).split( "\n" );

            for( int i = 0; i < lines.length; i++ ) lines[i] = ( " " + lines[i] ).split( "#" )[0];

        //------------------------------------------------------------------------------------------

            String content = String.join( "\n" , lines ).split( "---" )[1].split( "\\.\\.\\." )[0];

            content = content.replaceAll( "\n[^\n#]*Entries" , "\n- Entries" );
            content = content.replaceAll( "\n- Entries\\s*"  , "\n- Entries" );

            content = content.replaceAll( "]\\s*:"  , "]:"   );
            content = content.replaceAll( "\n\\s*-" , "\n-"  );
            content = content.replaceAll( ":\\s*-"  , ":\n-" );

        //------------------------------------------------------------------------------------------
            for( String section : content.split( "\n- Entries" ) ) {
        //------------------------------------------------------------------------------------------

                section = StringUtils.trimToEmpty( section );

            //--------------------------------------------------------------------------------------
                if( section.isEmpty() ) continue;
            //--------------------------------------------------------------------------------------

                Boolean noEntries = section.startsWith( "[" ) && section.endsWith( "]" );

                if( noEntries ) groups.add( section.replaceAll( "="   , ":" )
                        .replaceAll( "\\[" , "{" )
                        .replaceAll( "]"   , "}" ) );
                if( noEntries ) continue;

            //--------------------------------------------------------------------------------------

                if( !section.contains( "\n-" ) ) continue;

                String header =         StringUtils.trim( (section + "\n").split("\n-", 2)[0] );
                String body   = "\n-" + StringUtils.trim( (section + "\n").split("\n-", 2)[1] );

            //--------------------------------------------------------------------------------------
                for( String subsection : body.split( "\n-" ) ) {
            //--------------------------------------------------------------------------------------

                    if( StringUtils.trimToEmpty( subsection ).isEmpty() ) continue;

                //----------------------------------------------------------------------------------

                    groups.add( header.replaceAll( "]:"   , "}" + div )
                            .replaceAll( "^:"   , ""  )
                            .replaceAll( "="    , ":" )
                            .replaceAll( "\\["  , "{" )
                            .replaceAll( "]"    , "}" )
                            + "{" + subsection.replaceAll( "\n\\s*(\\w)" , ",$1" ) + "}" );

        //------------------------------------------------------------------------------------------
            } }
        //──────────────────────────────────────────────────────────────────────────────────────────
        // Then we find all the item stacks that match the groups
        //──────────────────────────────────────────────────────────────────────────────────────────

            Set<String> IDs = new HashSet<>();

        //------------------------------------------------------------------------------------------
            for( String group : groups ) {
        //------------------------------------------------------------------------------------------

                String defSection = group.contains( div ) ? group.split( div )[0] : "{}" ;
                String norSection = group.contains( div ) ? group.split( div )[1] : group;

                Gson gson = new Gson();

                Map<String, Object> json = new HashMap<>();
                json.putAll( gson.fromJson( defSection , Map.class ) );
                json.putAll( gson.fromJson( norSection , Map.class ) );

            //--------------------------------------------------------------------------------------
                json.replaceAll( ( s , o ) -> { try {
                    //--------------------------------------------------------------------------------------
                    if ( o instanceof Double ) {
                        //----------------------------------------------------------------------------------

                        return ( (Double) json.get( s ) ).intValue();

                        //----------------------------------------------------------------------------------
                    } if ( o instanceof Map ) {
                        //----------------------------------------------------------------------------------

                        return JsonToNBT.getTagFromJson( "" + gson.toJsonTree( o ) );

                        //----------------------------------------------------------------------------------
                    } return o;
                    //--------------------------------------------------------------------------------------
                } catch( NBTException ex ) { ex.printStackTrace(); return o; } } );
            //--------------------------------------------------------------------------------------

                List<Item> items = new ArrayList<>( ForgeRegistries.ITEMS.getValues() );

            //--------------------------------------------------------------------------------------
                if( json.containsKey( "Mod" ) ) {
            //--------------------------------------------------------------------------------------

                    items.removeIf( s -> !s.getRegistryName()
                            .getResourceDomain()
                            .equals( json.get( "Mod" ) ) );

            //--------------------------------------------------------------------------------------
                } if( json.containsKey( "Entry" ) ) {
            //--------------------------------------------------------------------------------------

                    items.removeIf( s -> !s.getRegistryName()
                            .getResourcePath()
                            .equals( json.get( "Entry" ) ) );

            //--------------------------------------------------------------------------------------
                } for( Item item : items ) {
            //--------------------------------------------------------------------------------------

                    CreativeTabs      tab = item.getCreativeTab();
                    if( null == tab ) tab = CreativeTabs.CREATIVE_TAB_ARRAY[0];

                //----------------------------------------------------------------------------------

                    NonNullList<ItemStack> stacks = NonNullList.create();
                    item.getSubItems( tab , stacks );

                //----------------------------------------------------------------------------------
                    if( json.containsKey( "Meta" ) ) {
                //----------------------------------------------------------------------------------

                        stacks.removeIf( s -> !json.get( "Meta" ).equals( s.getMetadata() ) );

                //----------------------------------------------------------------------------------
                    } if( json.containsKey( "NBT" ) ) {
                //----------------------------------------------------------------------------------

                        stacks.removeIf( s -> !s.hasTagCompound() );

                        stacks.removeIf( s -> !s.getTagCompound()
                                .toString()
                                .replace( " " , "" )
                                .toLowerCase()
                                .equals( json.get( "NBT" )
                                        .toString()
                                        .replace( " " , "" )
                                        .toLowerCase() ) );

                //----------------------------------------------------------------------------------
                    } for( ItemStack stack : stacks ) {
                //----------------------------------------------------------------------------------

                        Integer Width  = json.containsKey("Width" ) ? (int) json.get("Width" ) :  9;
                        Integer Height = json.containsKey("Height") ? (int) json.get("Height") :  3;
                        String  Mod    = stack.getItem().getRegistryName().getResourceDomain();
                        String  Entry  = stack.getItem().getRegistryName().getResourcePath();
                        Integer Meta   = stack.getMetadata();

                    //------------------------------------------------------------------------------

                        String ID = ""+ Width + Height + Mod + Entry + Meta +stack.getTagCompound();

                    //------------------------------------------------------------------------------
                        if( IDs.contains( ID ) ) continue;
                    //------------------------------------------------------------------------------

                        IDs.add( ID );

                    //------------------------------------------------------------------------------

                        NBTTagCompound compressions = new NBTTagCompound();

                        compressions.setInteger( "Width"  , Width    );
                        compressions.setInteger( "Height" , Height   );
                        compressions.setString ( "Mod"    , Mod      );
                        compressions.setString ( "Entry"  , Entry    );
                        compressions.setInteger( "Meta"   , Meta     );

                        if( stack.hasTagCompound() )
                            compressions.setTag( "NBT" , stack.getTagCompound() );

                        if( !stack.hasTagCompound() )
                            compressions.setTag( "NBT" , new NBTTagCompound() );

                    //------------------------------------------------------------------------------

                        if( !stack.hasTagCompound() ) stack.setTagCompound( new NBTTagCompound() );

                        stack.getTagCompound().setTag( "Compression" , compressions );

                    //------------------------------------------------------------------------------

                        entries.add( stack );

        //------------------------------------------------------------------------------------------
            } } }
        //------------------------------------------------------------------------------------------
        } catch( IOException ex ) { ex.printStackTrace(); } }

    //==============================================================================================
    // Usage
    //==============================================================================================

        public static Boolean getSettingsDarker() {
        //------------------------------------------------------------------------------------------

            Configuration file = new Configuration( new File( root + Settings ) );

        //------------------------------------------------------------------------------------------
            file.load();
        //------------------------------------------------------------------------------------------

            Boolean darker = file.getBoolean( "Darken" , "Options" , true , "Whether to " +
                    "have the images of higher levels get progressively darker" );

        //------------------------------------------------------------------------------------------
            file.save();
        //------------------------------------------------------------------------------------------

            return darker;

        //------------------------------------------------------------------------------------------
        }

    //==============================================================================================

    }

//==================================================================================================
