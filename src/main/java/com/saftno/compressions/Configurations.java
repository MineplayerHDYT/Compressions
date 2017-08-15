//==============================================================================================

    package com.saftno.compressions;

//==============================================================================================

    import com.google.gson.JsonObject;
    import com.google.gson.JsonParser;
    import com.sun.org.apache.xpath.internal.operations.Bool;
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

//==============================================================================================

    import java.io.File;
    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.List;

//==============================================================================================
    @SuppressWarnings( { "WeakerAccess" , "unused" } )
//==============================================================================================

    public class Configurations {

    //==========================================================================================
    // Structure
    //==========================================================================================

        public static class Entry {

        //======================================================================================
        // Structure
        //======================================================================================

            Integer Width  = 9;
            Integer Height = 3;
            String  Mod    = null;
            String  Entry  = null;
            Integer Meta   = null;
            String  NBT    = null;

        //======================================================================================
        // Unique identification
        //======================================================================================

            @Override public boolean equals( Object object ) {
            //----------------------------------------------------------------------------------
                if( !( object instanceof Entry ) ) return false;
            //---------------------------------------------------------------------------------

                Entry other = (Entry) object;

            //---------------------------------------------------------------------------------

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

            //----------------------------------------------------------------------------------
                return true;
            //----------------------------------------------------------------------------------
            }

            @Override public int hashCode() {
            //----------------------------------------------------------------------------------

                return  Width.hashCode() ^ Height.hashCode() ^
                        ( null == Mod    ? "".hashCode() : Mod.hashCode()   ) ^
                        ( null == Entry  ? "".hashCode() : Entry.hashCode() ) ^
                        ( null == Meta   ? "".hashCode() : Meta.hashCode()  ) ^
                        ( null == NBT    ? "".hashCode() : NBT.hashCode()   ) ;

            //----------------------------------------------------------------------------------
            }

        //======================================================================================
        // Helpers
        //======================================================================================

            public void Parse( String content ) { try {
            //----------------------------------------------------------------------------------

                content = StringUtils.trim( content );

                content = content.replaceAll( ":$"  , ""  );
                content = content.replaceAll( "="   , ":" );
                content = content.replaceAll( "^\\[" , "{" );
                content = content.replaceAll( "]$"  , "}" );

            //----------------------------------------------------------------------------------

                content = StringUtils.trim( content );

                if( !content.startsWith( "{" ) ) content = "{" + content + "}";

                content = content.replaceAll( "\n\\s*(\\w)" , ",$1" );

            //----------------------------------------------------------------------------------

                JsonObject in = new JsonParser().parse( content ).getAsJsonObject();

            //----------------------------------------------------------------------------------

                Boolean width  = in.has( "Width"  );
                Boolean height = in.has( "Height" );
                Boolean mod    = in.has( "Mod"    );
                Boolean entry  = in.has( "Entry"  );
                Boolean meta   = in.has( "Meta"   );
                Boolean nbt    = in.has( "NBT"    );

            //----------------------------------------------------------------------------------

                if( width  ) Width  = Integer.parseInt( in.get( "Width"  ).getAsString() );
                if( height ) Height = Integer.parseInt( in.get( "Height" ).getAsString() );
                if( meta   ) Meta   = Integer.parseInt( in.get( "Meta"   ).getAsString() );
                if( mod    ) Mod    = in.get( "Mod"   ).getAsString();
                if( entry  ) Entry  = in.get( "Entry" ).getAsString();
                if( nbt    ) NBT    = JsonToNBT.getTagFromJson("" + in.get( "NBT")).toString();

            //----------------------------------------------------------------------------------
            } catch ( NBTException ex ) { ex.printStackTrace(); } }

        //======================================================================================

            public String NBTAsExtraDescription() {
            //----------------------------------------------------------------------------------

                if(      null == NBT      ) return "";
                if( !NBT.contains( "\"" ) ) return "";

            //----------------------------------------------------------------------------------
                NBT = NBT.split( "\"" )[1];
            //----------------------------------------------------------------------------------

                if( !NBT.contains( ":" ) ) return " (" + StringUtils.capitalize( NBT ) + ")";

            //----------------------------------------------------------------------------------
                ResourceLocation id = new ResourceLocation( StringUtils.trim( NBT ) );
            //----------------------------------------------------------------------------------

                EntityEntry entity      = ForgeRegistries.ENTITIES.getValue( id );
                Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue( id );
                PotionType  potiontype  = ForgeRegistries.POTION_TYPES.getValue( id );

            //----------------------------------------------------------------------------------

                if( null !=   entity    ) return "";
                if( null != enchantment ) return " " + enchantment.getTranslatedName( 0 );

            //----------------------------------------------------------------------------------
                if( !potiontype.getRegistryName().getResourcePath().equals( "empty" ) ) {
            //----------------------------------------------------------------------------------

                    if( 0 == potiontype.getEffects().size() ) return "";

                //------------------------------------------------------------------------------

                    PotionEffect effect = potiontype.getEffects().get( 0 );

                    Integer sec = ( effect.getDuration() / 20 ) % 60;
                    Integer min = ( effect.getDuration() / 20 ) / 60;
                    Integer amp = effect.getAmplifier();

                    String secS = ( sec < 10 ? "0" : "" ) + sec;
                    String ampS = ( amp == 0 ) ? "" : ( " x" + ( 1 + amp ) ) ;

                    return " ( " + min + ":" + secS + " )" + ampS;

            //----------------------------------------------------------------------------------
                } return "";
            //----------------------------------------------------------------------------------
            }

        //======================================================================================
        // Usage
        //======================================================================================

            public Entry() {}
            public Entry( String content ) { this.Parse( content ); }

        //======================================================================================

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

        //======================================================================================

        }

    //==========================================================================================

        public static String root = Base.root + "/config/compressions/";

    //==========================================================================================

        public static File Entries  = new File( root + "entries.cfg"  );
        public static File Settings = new File( root + "settings.cfg" );

    //==========================================================================================
    // Setup
    //==========================================================================================

        static /* Create files */ { init: { try {
        //--------------------------------------------------------------------------------------

            final byte[] entries = String.join( "\n" , new String[] {
"#===================================================================================================",
"--- # Add items to have compression here, between the --- and ...",
"#===================================================================================================",
"",
"     Entries:",
"",
"#===================================================================================================",
"... # You can have as many 'Entries:' blocks as you want (They will all load with no duplicates)",
"#===================================================================================================",
"#                                         Examples",
"#===================================================================================================",
"#                         ┌───┐",
"#    Entries[ ... ]:      │ ← │    Default values for all entries in this block",
"#                         │   │",
"#        - Width:  ...    │ ← │    [9:  2 ~ 9] - The amount of items per pack",
"#          Height: ...    │ ← │    [3:  0 ~ 8] - How many levels of compression to have",
"#          Mod:    ...    │ ← │    [:    text] - The name of the mod",
"#          Entry:  ...    │ ← │    [:    text] - The name of the item/block",
"#          Meta:   ...    │ ← │    [0: 0 ~ 15] - The variant of the item/block",
"#          NBT:    ...    │ ← │    [:    JSON] - The NBT tag of the item/block",
"#                         └───┘",
"#===================================================================================================",
"",
"    Entries:",
"",
"        - Mod:   minecraft",
"          Entry: cobblestone",
"",
"        - Mod:   minecraft",
"          Entry: sand",
"          Meta:  0",
"",
"        - Mod:   minecraft",
"          Entry: gravel",
"",
"        - Mod:   minecraft",
"          Entry: dirt",
"          Meta:  0",
"",
"#===================================================================================================",
"# The defaults section makes adding entries more convenient",
"#===================================================================================================",
"",
"     Entries[Mod = minecraft, Meta = 0]:",
"",
"         - Entry: cobblestone",
"         - Entry: sand",
"         - Entry: gravel",
"         - Entry: dirt",
"",
"#===================================================================================================",
"# Capitalization in the NBT tag doesn't matter (But it does in the quoted parts)",
"#===================================================================================================",
"",
"     Entries[ Mod = minecraft , Entry = potion ]: - NBT: { pOtIoN: ¤minecraft:strong_healing¤    }",
"                                                  - NBT: { Potion: ¤minecraft:long_regeneration¤ }",
"                                                  - NBT: { potion: ¤minecraft:long_strength¤     }",
"",
"     Entries[ Mod   = minecraft",
"            , Entry = spawn_egg ]:",
"",
"         - NBT: { EntityTag: { ID: ¤minecraft:pig¤    } }",
"         - NBT: { EntityTag: { id: ¤minecraft:zombie¤ } }",
"",
"#===================================================================================================",
"# You can have everything in the defaults section (Just remove the ':' from the end)",
"#===================================================================================================",
"",
"     Entries[ Width  = 4",
"            , Height = 2",
"            , Mod    = storagedrawers",
"            , Entry  = basicdrawers",
"            , NBT    = { material: ¤birch¤ } ]",
"",
"#==================================================================================================="
            } ).replace( "¤" , "\"" ).getBytes();

            if( !Entries.exists() ) FileUtils.writeByteArrayToFile( Entries , entries );

        //--------------------------------------------------------------------------------------

            if( !Settings.exists() ) FileUtils.touch( Settings );

            getSettingsDarker();

        //--------------------------------------------------------------------------------------
        } catch( IOException ex ) { ex.printStackTrace(); } } }

    //==========================================================================================
    // Usage
    //==========================================================================================

        public static List<Entry> getEntries() { try {
        //--------------------------------------------------------------------------------------
            List<Entry> entries = new ArrayList<>();
        //--------------------------------------------------------------------------------------

            String[] lines = FileUtils.readFileToString( Entries , "utf8" ).split( "\n" );

            for( int i = 0; i < lines.length; i++ ) lines[i] = (" " + lines[i]).split("#")[0];

        //--------------------------------------------------------------------------------------

            String content = String.join( "\n" , lines ).split("---")[1].split("\\.\\.\\.")[0];

            content = content.replaceAll( "\n[^\n#]*Entries" , "\n- Entries" );
            content = content.replaceAll( "\n- Entries\\s*"  , "\n- Entries" );

            content = content.replaceAll( "]\\s*:"  , "]:"   );
            content = content.replaceAll( "\n\\s*-" , "\n-"  );
            content = content.replaceAll( ":\\s*-"  , ":\n-" );

        //--------------------------------------------------------------------------------------
            for( String section : content.split( "\n- Entries" ) ) {
        //--------------------------------------------------------------------------------------

                section = StringUtils.trimToEmpty( section );

            //----------------------------------------------------------------------------------
                if( section.isEmpty() ) continue;
            //----------------------------------------------------------------------------------

                Boolean noEntries = section.startsWith( "[" ) && section.endsWith( "]" );

                if( noEntries ) entries.add( new Entry( section ) );
                if( noEntries ) continue;

            //----------------------------------------------------------------------------------

                if( !section.contains( "\n-" ) ) continue;

                String header =         StringUtils.trim( (section + "\n").split("\n-", 2)[0] );
                String body   = "\n-" + StringUtils.trim( (section + "\n").split("\n-", 2)[1] );

            //----------------------------------------------------------------------------------

                Entry def = new Entry( header );

            //----------------------------------------------------------------------------------
                for( String subsection : body.split( "\n-" ) ) {
            //----------------------------------------------------------------------------------

                    if( StringUtils.trimToEmpty( subsection ).isEmpty() ) continue;

                //------------------------------------------------------------------------------

                    entries.add( new Entry( def , subsection ) );

        //--------------------------------------------------------------------------------------
            } } return entries;
        //--------------------------------------------------------------------------------------
        } catch( IOException ex ) { ex.printStackTrace(); return null; } }

    //==========================================================================================

        public static Boolean getSettingsDarker() {

            Configuration file = new Configuration( Settings );

        //--------------------------------------------------------------------------------------
            file.load();
        //--------------------------------------------------------------------------------------

            Boolean darker = file.getBoolean( "Darken" , "Options" , true , "Whether to " +
                    "have the images of higher levels get progressively darker" );

        //--------------------------------------------------------------------------------------
            file.save();
        //--------------------------------------------------------------------------------------

            return darker;

        //--------------------------------------------------------------------------------------
        }

    //==========================================================================================

    }

//==============================================================================================