//==============================================================================================

    package com.saftno.compressions;

//==============================================================================================

    import com.esotericsoftware.yamlbeans.YamlException;
    import com.esotericsoftware.yamlbeans.YamlReader;

//==============================================================================================

    import net.minecraft.enchantment.Enchantment;
    import net.minecraft.potion.PotionEffect;
    import net.minecraft.potion.PotionType;
    import net.minecraft.util.ResourceLocation;
    import net.minecraftforge.fml.common.registry.EntityEntry;
    import net.minecraftforge.fml.common.registry.ForgeRegistries;
    import org.apache.commons.io.FileUtils;
    import org.apache.commons.lang3.StringUtils;

//==============================================================================================

    import java.io.File;
    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.HashMap;
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
        // Usage
        //======================================================================================

            public Entry() {}

        //======================================================================================

            public Entry( String content ) { try {
            //----------------------------------------------------------------------------------
                if( !content.contains( "[" ) ) return;
            //----------------------------------------------------------------------------------

                content = content.split( "\\[" , 2 )[1];

                content = content.replaceAll(   "="   ,  ":"  );
                content = content.replaceAll(  "\\["  ,  ""   );
                content = content.replaceAll(   "]$"  ,  ""   );
                content = content.replaceAll( ",\\s*" ,  ","  );
                content = content.replaceAll(   ","   , "\n"  );

            //----------------------------------------------------------------------------------

                HashMap<String, Object> in = (HashMap) new YamlReader( content ).read();

            //----------------------------------------------------------------------------------

                Boolean width  = in.containsKey( "Width"  );
                Boolean height = in.containsKey( "Height" );
                Boolean mod    = in.containsKey( "Mod"    );
                Boolean entry  = in.containsKey( "Entry"  );
                Boolean meta   = in.containsKey( "Meta"   );
                Boolean nbt    = in.containsKey( "NBT"    );

            //----------------------------------------------------------------------------------

                if( width  ) Width  = Integer.parseInt( (String) in.get( "Width"  ) );
                if( height ) Height = Integer.parseInt( (String) in.get( "Height" ) );
                if( meta   ) Meta   = Integer.parseInt( (String) in.get( "Meta"   ) );
                if( mod    ) Mod    = (String) in.get( "Mod"   );
                if( entry  ) Entry  = (String) in.get( "Entry" );
                if( nbt    ) NBT    = (String) in.get( "NBT"   );

            //----------------------------------------------------------------------------------
            } catch( YamlException ex ) { ex.printStackTrace(); } }

        //======================================================================================

            public Entry( Entry another , HashMap<String , Object> in ) {
            //----------------------------------------------------------------------------------

                this.Width  = another.Width;
                this.Height = another.Height;
                this.Mod    = another.Mod;
                this.Entry  = another.Entry;
                this.Meta   = another.Meta;
                this.NBT    = another.NBT;

            //----------------------------------------------------------------------------------

                Boolean width  = in.containsKey( "Width"  );
                Boolean height = in.containsKey( "Height" );
                Boolean mod    = in.containsKey( "Mod"    );
                Boolean entry  = in.containsKey( "Entry"  );
                Boolean meta   = in.containsKey( "Meta"   );
                Boolean nbt    = in.containsKey( "NBT"    );

            //----------------------------------------------------------------------------------

                if( width  ) Width  = Integer.parseInt( (String) in.get( "Width"  ) );
                if( height ) Height = Integer.parseInt( (String) in.get( "Height" ) );
                if( meta   ) Meta   = Integer.parseInt( (String) in.get( "Meta"   ) );
                if( mod    ) Mod    = (String) in.get( "Mod"   );
                if( entry  ) Entry  = (String) in.get( "Entry" );
                if( nbt    ) NBT    = (String) in.get( "NBT"   );

            //----------------------------------------------------------------------------------
            }

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

        }

    //==========================================================================================

        public static String root = Base.root + "/config/compressions/";

    //==========================================================================================

        public static File Readme  = new File( root + "README.txt");
        public static File Entries = new File( root + "entries.yaml");

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
"#",
"#    Entries:",
"#                                 ┌───┐",
"#        - Width:  [9:  2 ~ 9]    │ ← │    The amount of items per pack",
"#          Height: [3:  0 ~ 8]    │ ← │    How many levels of compression to have",
"#          Mod:    [:    text]    │ ← │    The name of the mod",
"#          Entry:  [:    text]    │ ← │    The name of the item/block",
"#          Meta:   [0: 0 ~ 15]    │ ← │    The variant of the item/block",
"#          NBT:    [:    text]    │ ← │    The NBT tag of the item/block",
"#                                 └───┘",
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
"",
"     Entries[Mod = minecraft, Meta = 0]:",
"",
"         - Entry: cobblestone",
"         - Entry: sand",
"         - Entry: gravel",
"         - Entry: dirt",
"",
"#===================================================================================================",
"",
"     Entries[Width = 5, Mod = minecraft, Meta = 0]: [ { Entry: cobblestone }",
"                                                    , { Entry: sand        }",
"                                                    , { Entry: gravel      }",
"                                                    , { Entry: dirt        } ]",
"",
"#===================================================================================================",
"",
"     Entries[Mod = minecraft, Entry = potion]:",
"                                                 ",
"         - NBT: '{ pOtIoN: ~minecraft:strong_healing~    }'",
"         - NBT: '{ Potion: ~minecraft:long_regeneration~ }'",
"         - NBT: '{ potion: ~minecraft:long_strength~     }'",
"",
"     Entries[Mod = minecraft, Entry = spawn_egg]:",
"",
"         - NBT: '{ EntityTag: { ID: ~minecraft:pig~    } }'",
"         - NBT: '{ EntityTag: { id: ~minecraft:zombie~ } }'",
"",
"#===================================================================================================",
"",
"     Entries:",
"",
"         - Width:  4",
"           Height: 2",
"           Mod:    storagedrawers",
"           Entry:  basicdrawers",
"           Meta:   2",
"           NBT:    '{ material: ~spruce~ }'",
"",
"#==================================================================================================="
            } ).replace( "~" , "\"" ).getBytes();

            if( !Entries.exists() ) FileUtils.writeByteArrayToFile( Entries , entries );

        //--------------------------------------------------------------------------------------
        } catch( IOException ex ) { ex.printStackTrace(); } } }

    //==========================================================================================
    // Usage
    //==========================================================================================

        public static List<Entry> getEntries() { try {
        //--------------------------------------------------------------------------------------
            List<Entry> entries = new ArrayList<>();
        //--------------------------------------------------------------------------------------

            String content = FileUtils.readFileToString( Entries , "utf8" );

            content = content.replaceAll( "\n[^\n#]*Entries" , "\n- Entries" );
            content = content.replaceAll( "\n- Entries\\s*" , "\n- Entries" );

        //--------------------------------------------------------------------------------------
            for( Object object : (ArrayList) new YamlReader( content ).read() ) {
        //--------------------------------------------------------------------------------------

                HashMap entriesBlock = (HashMap) object;
                Entry   defaultEntry = new Entry( (String) entriesBlock.keySet().toArray()[0] );

            //----------------------------------------------------------------------------------

                if( null == entriesBlock.values().toArray()[0] ) continue;

            //----------------------------------------------------------------------------------
                for( Object entryObj : (List) entriesBlock.values().toArray()[0] ) {
            //----------------------------------------------------------------------------------

                    entries.add( new Entry( defaultEntry , (HashMap) entryObj ) );

        //----------------------------------------------------------------------------------
            } } return entries;
        //--------------------------------------------------------------------------------------
        } catch( IOException ex ) { ex.printStackTrace(); return null; } }

    //==========================================================================================

    }

//==============================================================================================