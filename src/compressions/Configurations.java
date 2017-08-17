//==================================================================================================

    package compressions;

//==================================================================================================

    import com.google.gson.JsonObject;
    import com.google.gson.JsonParser;
    import net.minecraft.enchantment.Enchantment;
    import net.minecraft.nbt.JsonToNBT;
    import net.minecraft.nbt.NBTException;
    import net.minecraft.potion.PotionEffect;
    import net.minecraft.potion.PotionType;
    import net.minecraft.util.ResourceLocation;
    import net.minecraftforge.common.config.Configuration;
    import net.minecraftforge.fml.common.registry.EntityEntry;
    import net.minecraftforge.fml.common.registry.ForgeRegistries;
    import org.apache.commons.io.FileUtils;
    import org.apache.commons.lang3.StringUtils;

//==================================================================================================

    import java.io.File;
    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Paths;
    import java.util.ArrayList;
    import java.util.List;

//==================================================================================================

    public class Configurations {

    //==============================================================================================
        public static String root = Base.root + "/config/compressions/";
    //==============================================================================================

        public static String Entries  = "entries.yaml";
        public static String Settings = "settings.cfg";

    //==============================================================================================

        public static class Entry {

        //==========================================================================================
        // Structure
        //==========================================================================================

            Integer Width  = 9;
            Integer Height = 3;
            String  Mod    = null;
            String  Entry  = null;
            Integer Meta   = null;
            String  NBT    = null;

        //==========================================================================================
        // Unique identification
        //==========================================================================================

            @Override public boolean equals( Object object ) {
            //--------------------------------------------------------------------------------------
                if( !( object instanceof Entry ) ) return false;
            //--------------------------------------------------------------------------------------

                Entry other = (Entry) object;

            //--------------------------------------------------------------------------------------

                if( !this.Width.equals ( other.Width  ) ) return false;
                if( !this.Height.equals( other.Height ) ) return false;

                if( null == this.Mod && null != other.Mod ) return false;
                if( null != this.Mod && null == other.Mod ) return false;
                if( null != this.Mod && !this.Mod.equals( other.Mod ) ) return false;

                if( null == this.Entry && null != other.Entry ) return false;
                if( null != this.Entry && null == other.Entry ) return false;
                if( null != this.Entry && !this.Entry.equals( other.Entry ) ) return false;

                if( null == this.Meta && null != other.Meta ) return false;
                if( null != this.Meta && null == other.Meta ) return false;
                if( null != this.Meta && !this.Meta.equals( other.Meta ) ) return false;

                if( null == this.NBT && null != other.NBT ) return false;
                if( null != this.NBT && null == other.NBT ) return false;
                if( null != this.NBT && !this.NBT.equals( other.NBT ) ) return false;

            //--------------------------------------------------------------------------------------
                return true;
            //--------------------------------------------------------------------------------------
            }

            @Override public int hashCode() {
            //--------------------------------------------------------------------------------------

                return  Width.hashCode() ^ Height.hashCode() ^
                        ( null == Mod    ? "".hashCode() : Mod.hashCode()   ) ^
                        ( null == Entry  ? "".hashCode() : Entry.hashCode() ) ^
                        ( null == Meta   ? "".hashCode() : Meta.hashCode()  ) ^
                        ( null == NBT    ? "".hashCode() : NBT.hashCode()   ) ;

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
        // Helpers
        //==========================================================================================

            public void Parse( String content ) { try {
            //--------------------------------------------------------------------------------------

                content = StringUtils.trim( content );

                content = content.replaceAll( ":$"  , ""  );
                content = content.replaceAll( "="   , ":" );
                content = content.replaceAll( "^\\[" , "{" );
                content = content.replaceAll( "]$"  , "}" );

            //--------------------------------------------------------------------------------------

                content = StringUtils.trim( content );

                if( !content.startsWith( "{" ) ) content = "{" + content + "}";

                content = content.replaceAll( "\n\\s*(\\w)" , ",$1" );

            //--------------------------------------------------------------------------------------

                JsonObject in = new JsonParser().parse( content ).getAsJsonObject();

            //--------------------------------------------------------------------------------------

                Boolean width  = in.has( "Width"  );
                Boolean height = in.has( "Height" );
                Boolean mod    = in.has( "Mod"    );
                Boolean entry  = in.has( "Entry"  );
                Boolean meta   = in.has( "Meta"   );
                Boolean nbt    = in.has( "NBT"    );

            //--------------------------------------------------------------------------------------

                if( width  ) Width  = Integer.parseInt( in.get( "Width"  ).getAsString() );
                if( height ) Height = Integer.parseInt( in.get( "Height" ).getAsString() );
                if( meta   ) Meta   = Integer.parseInt( in.get( "Meta"   ).getAsString() );
                if( mod    ) Mod    = in.get( "Mod"   ).getAsString();
                if( entry  ) Entry  = in.get( "Entry" ).getAsString();
                if( nbt    ) NBT    = JsonToNBT.getTagFromJson( "" + in.get( "NBT") ).toString();

            //--------------------------------------------------------------------------------------
            } catch ( NBTException ex ) { ex.printStackTrace(); } }

        //==========================================================================================

            public String NBTAsExtraDescription() {
            //--------------------------------------------------------------------------------------

                if(      null == NBT      ) return "";
                if( !NBT.contains( "\"" ) ) return "";

            //--------------------------------------------------------------------------------------
                NBT = NBT.split( "\"" )[1];
            //--------------------------------------------------------------------------------------

                if( !NBT.contains( ":" ) ) return " (" + StringUtils.capitalize( NBT ) + ")";

            //--------------------------------------------------------------------------------------
                ResourceLocation id = new ResourceLocation( StringUtils.trim( NBT ) );
            //--------------------------------------------------------------------------------------

                EntityEntry entity      = ForgeRegistries.ENTITIES.getValue( id );
                Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue( id );
                PotionType potiontype  = ForgeRegistries.POTION_TYPES.getValue( id );

            //--------------------------------------------------------------------------------------

                if( null !=   entity    ) return "";
                if( null != enchantment ) return " " + enchantment.getTranslatedName( 0 );

            //--------------------------------------------------------------------------------------
                if( !potiontype.getRegistryName().getResourcePath().equals( "empty" ) ) {
            //--------------------------------------------------------------------------------------

                    if( 0 == potiontype.getEffects().size() ) return "";

                //----------------------------------------------------------------------------------

                    PotionEffect effect = potiontype.getEffects().get( 0 );

                    Integer sec = ( effect.getDuration() / 20 ) % 60;
                    Integer min = ( effect.getDuration() / 20 ) / 60;
                    Integer amp = effect.getAmplifier();

                    String secS = ( sec < 10 ? "0" : "" ) + sec;
                    String ampS = ( amp == 0 ) ? "" : ( " x" + ( 1 + amp ) ) ;

                    return " ( " + min + ":" + secS + " )" + ampS;

            //--------------------------------------------------------------------------------------
                } return "";
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
        // Usage
        //==========================================================================================

            public Entry() {}

        //==========================================================================================

            public Entry( String content ) { this.Parse( content ); }

        //==========================================================================================

            public Entry( Entry def , String content ) {
                //----------------------------------------------------------------------------------

                this.Width  = def.Width;
                this.Height = def.Height;
                this.Mod    = def.Mod;
                this.Entry  = def.Entry;
                this.Meta   = def.Meta;
                this.NBT    = def.NBT;

                //----------------------------------------------------------------------------------

                this.Parse( content );

                //----------------------------------------------------------------------------------
            }

        //==========================================================================================

        }

    //==============================================================================================

        static /* Set up the config files */ { init: try {
        //------------------------------------------------------------------------------------------
            if( new File( root + Entries ).exists() ) break init;
        //------------------------------------------------------------------------------------------

            Files.copy( Base.class.getResourceAsStream("/" + Entries) , Paths.get(root + Entries) );

        //------------------------------------------------------------------------------------------
        } catch( IOException ex ) { ex.printStackTrace(); } }

    //==============================================================================================

        public static List<Entry> getEntries() { try {
        //------------------------------------------------------------------------------------------
            List<Entry> entries = new ArrayList<>();
        //------------------------------------------------------------------------------------------

            String[] lines = FileUtils.readFileToString( new File( root + Entries ) , "utf8" )
                                      .split( "\n" );

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

                if( noEntries ) entries.add( new Entry( section ) );
                if( noEntries ) continue;

            //--------------------------------------------------------------------------------------

                if( !section.contains( "\n-" ) ) continue;

                String header =         StringUtils.trim( (section + "\n").split("\n-", 2)[0] );
                String body   = "\n-" + StringUtils.trim( (section + "\n").split("\n-", 2)[1] );

            //--------------------------------------------------------------------------------------

                Entry def = new Entry( header );

            //--------------------------------------------------------------------------------------
                for( String subsection : body.split( "\n-" ) ) {
            //--------------------------------------------------------------------------------------

                    if( StringUtils.trimToEmpty( subsection ).isEmpty() ) continue;

                //----------------------------------------------------------------------------------

                    entries.add( new Entry( def , subsection ) );

        //------------------------------------------------------------------------------------------
            } } return entries;
        //------------------------------------------------------------------------------------------
        } catch( IOException ex ) { ex.printStackTrace(); return null; } }

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
