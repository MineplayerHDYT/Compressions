//==================================================================================================

    package compressions;

//==================================================================================================

    import com.google.common.collect.Lists;
    import com.google.gson.JsonElement;
    import com.google.gson.JsonObject;
    import com.google.gson.JsonParser;
    import mcp.MethodsReturnNonnullByDefault;
    import net.minecraft.block.material.Material;
    import net.minecraft.block.state.IBlockState;
    import net.minecraft.client.Minecraft;
    import net.minecraft.client.renderer.*;
    import net.minecraft.client.renderer.block.model.IBakedModel;
    import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
    import net.minecraft.client.renderer.block.model.ItemOverrideList;
    import net.minecraft.client.renderer.texture.*;
    import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
    import net.minecraft.client.renderer.vertex.VertexFormat;
    import net.minecraft.client.resources.*;
    import net.minecraft.client.resources.data.IMetadataSection;
    import net.minecraft.client.resources.data.MetadataSerializer;
    import net.minecraft.creativetab.CreativeTabs;
    import net.minecraft.inventory.InventoryCrafting;
    import net.minecraft.item.Item;
    import net.minecraft.item.ItemStack;
    import net.minecraft.item.crafting.Ingredient;
    import net.minecraft.item.crafting.ShapelessRecipes;
    import net.minecraft.nbt.JsonToNBT;
    import net.minecraft.nbt.NBTException;
    import net.minecraft.nbt.NBTTagCompound;
    import net.minecraft.util.*;
    import net.minecraft.world.World;
    import net.minecraftforge.client.model.ICustomModelLoader;
    import net.minecraftforge.client.model.IModel;
    import net.minecraftforge.common.model.IModelState;
    import net.minecraftforge.fml.common.Mod;
    import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
    import net.minecraftforge.fml.common.registry.ForgeRegistries;
    import net.minecraftforge.fml.relauncher.ReflectionHelper;
    import net.minecraftforge.fml.relauncher.Side;
    import net.minecraftforge.fml.relauncher.SideOnly;
    import org.apache.commons.io.FileUtils;
    import org.apache.commons.lang3.StringUtils;
    import org.apache.commons.lang3.tuple.ImmutablePair;
    import org.apache.commons.lang3.tuple.Pair;
    import org.lwjgl.opengl.*;

//=======================================================================================================

    import javax.annotation.Nullable;
    import javax.annotation.ParametersAreNonnullByDefault;
    import javax.vecmath.Matrix4f;
    import java.awt.image.BufferedImage;
    import java.io.File;
    import java.io.IOException;
    import java.io.InputStream;
    import java.lang.reflect.Field;
    import java.lang.reflect.ParameterizedType;
    import java.lang.reflect.Type;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.util.*;
    import java.util.function.Function;

//==================================================================================================
    @Mod.EventBusSubscriber   @MethodsReturnNonnullByDefault   @ParametersAreNonnullByDefault
//--------------------------------------------------------------------------------------------------
    @Mod( modid   = Base.modId   , acceptedMinecraftVersions = "[1.12 , 1.13)",
          name    = Base.name    ,
          version = Base.version )//*/
//--------------------------------------------------------------------------------------------------
    @SuppressWarnings( { "WeakerAccess" , "CanBeFinal" , "unused" } )
//==================================================================================================

    public class Base {

    /*==============================================================================================
    // Forge
    //============================================================================================*/

        public static final String modId   = "compressions";
        public static final String name    = "Compressions";
        public static final String version = "4.0.5";

    //==============================================================================================
        @Mod.Instance( modId )
    //==============================================================================================

        public static Base instance;

    /*==============================================================================================
    // Utility
    //============================================================================================*/

        public static Path   root = Paths.get( System.getProperty( "user.dir" ) );
        public static String texs = Base.modId + "_textures";

    /*==============================================================================================
    // Resource pack section
    //============================================================================================*/

        /*******************************************************************************************
         *
         *   Resource pack to load textures from outside the assets folder (But inside the mod)
         *
         ******************************************************************************************/

        public static class ResourcePack implements IResourcePack {

        //==========================================================================================

            @Nullable @Override public InputStream getInputStream(
            //--------------------------------------------------------------------------------------
                    ResourceLocation location
            //--------------------------------------------------------------------------------------
            ) throws IOException {
            //--------------------------------------------------------------------------------------

                String name = StringUtils.removeStart( location.getResourcePath() , "textures/" );

                return this.getClass().getResourceAsStream( "/" + name );

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public boolean resourceExists( ResourceLocation location ) {
            //--------------------------------------------------------------------------------------
                try  { return null != this.getInputStream( location ); }
            //--------------------------------------------------------------------------------------
                catch( IOException ignored ) { } return false;
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public Set<String> getResourceDomains() {
            //--------------------------------------------------------------------------------------
                return new HashSet<>( Arrays.asList( Base.texs ) );
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Nullable @Override public <T extends IMetadataSection> T getPackMetadata(
            //--------------------------------------------------------------------------------------
                      MetadataSerializer metadataSerializer
                    , String             metadataSectionName
            //--------------------------------------------------------------------------------------
            ) throws IOException {
            //--------------------------------------------------------------------------------------

                String mcmeta = "{ 'pack' : { 'pack_format' : 3 , 'description' : '' } }";

                JsonObject json = new JsonParser().parse( mcmeta ).getAsJsonObject();

            //--------------------------------------------------------------------------------------
                return metadataSerializer.parseMetadataSection( "pack" , json );
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public BufferedImage getPackImage() throws IOException {
            //--------------------------------------------------------------------------------------
                return null;
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public String getPackName() {
            //--------------------------------------------------------------------------------------
                return texs;
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

        }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

        public static void RegisterResourcePacks() {
        //------------------------------------------------------------------------------------------
            for( Field field : Minecraft.class.getDeclaredFields() ) {
        //------------------------------------------------------------------------------------------

                field.setAccessible( true );

                Type listType = field.getGenericType();

            //--------------------------------------------------------------------------------------
                if ( listType instanceof ParameterizedType ) {
            //--------------------------------------------------------------------------------------

                    List<IResourcePack> defaultResourcePacks;

                    try  { defaultResourcePacks = (List) field.get( Minecraft.getMinecraft() ); }
                    catch( Exception e ) { continue; }

                    defaultResourcePacks.add( new ResourcePack() );

                    try  { field.set( Minecraft.getMinecraft() , defaultResourcePacks ); }
                    catch( Exception e ) { continue; }

                    return;

        //------------------------------------------------------------------------------------------
            } }
        //------------------------------------------------------------------------------------------

//            String textureName = "entitytest.png";
//            File textureFile = null;
//            try { textureFile = new File(Minecraft.getMinecraft().mcDataDir.getCanonicalPath(), textureName); } catch (Exception ex) {}
//
//            if (textureFile != null && textureFile.exists()) {
//                ResourceLocation MODEL_TEXTURE = Resources.OTHER_TESTMODEL_CUSTOM;
//
//                TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
//                texturemanager.deleteTexture(MODEL_TEXTURE);
//                Object object = new ThreadDownloadImageData(textureFile, null, MODEL_TEXTURE, new ImageBufferDownload());
//                texturemanager.loadTexture(MODEL_TEXTURE, (ITextureObject)object);
//
//                return true;
//            } else {
//                return false;
//            }
//
//            DynamicTexture

        //------------------------------------------------------------------------------------------
        }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

        static /* Register the resource pack */ {
            RegisterResourcePacks();
        }

    /*==============================================================================================
    // Entry section
    //============================================================================================*/

        /*******************************************************************************************
         *
         *   A entry representing an addition to the game that is connected to an existing
         *   item/BLOCK.
         *
         ******************************************************************************************/

        public static abstract class Entry<T extends Entry> {

        /*==========================================================================================
        // Structure
        //========================================================================================*/

            public String         Mod;
            public String         Entry;
            public Integer        Meta;
            public NBTTagCompound NBT;

        //==========================================================================================

            public List<T> related;

        /*==========================================================================================
        // Unique identification
        //========================================================================================*/

            @Override public boolean equals( Object object ) {
            //--------------------------------------------------------------------------------------
                if( !( object instanceof Entry ) ) return false;
            //--------------------------------------------------------------------------------------

                Entry other = (Entry) object;

            //--------------------------------------------------------------------------------------

                if( null == this.Mod && null != other.Mod ) return false;
                if( null != this.Mod && null == other.Mod ) return false;
                if( null != this.Mod && !this.Mod.equals( other.Mod ) ) return false;

            // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

                if( null == this.Entry && null != other.Entry ) return false;
                if( null != this.Entry && null == other.Entry ) return false;
                if( null != this.Entry && !this.Entry.equals( other.Entry ) ) return false;

            // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

                if( null == this.Meta && null != other.Meta ) return false;
                if( null != this.Meta && null == other.Meta ) return false;
                if( null != this.Meta && !this.Meta.equals( other.Meta ) ) return false;

            // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

                if( null == this.NBT && null != other.NBT ) return false;
                if( null != this.NBT && null == other.NBT ) return false;
                if( null != this.NBT && !this.NBT.equals( other.NBT ) ) return false;

            //--------------------------------------------------------------------------------------
                return true;
            //--------------------------------------------------------------------------------------
            }

            @Override public int hashCode() {
            //----------------------------------------------------------------------------------

                return  ( null == Mod    ? "".hashCode() : Mod.hashCode()   ) ^
                        ( null == Entry  ? "".hashCode() : Entry.hashCode() ) ^
                        ( null == Meta   ? "".hashCode() : Meta.hashCode()  ) ^
                        ( null == NBT    ? "".hashCode() : NBT.hashCode()   ) ;

            //----------------------------------------------------------------------------------
            }

        /*==========================================================================================
        // Parser section
        //========================================================================================*/

            Entry() { }

            Entry( ItemStack stack ) {
            //--------------------------------------------------------------------------------------

                this.Mod   = stack.getItem().getRegistryName().getResourceDomain();
                this.Entry = stack.getItem().getRegistryName().getResourcePath();
                this.Meta  = stack.getMetadata();
                this.NBT   = stack.hasTagCompound() ? stack.getTagCompound() : null;

            //--------------------------------------------------------------------------------------
            }

        //========================================================================================*/

            Entry( NBTTagCompound tag ) {
            //--------------------------------------------------------------------------------------
                if( null == tag ) return;
            //--------------------------------------------------------------------------------------

                Class<?> clazz = this.getClass();

            //--------------------------------------------------------------------------------------
                while( clazz != null ) { for( Field field : clazz.getDeclaredFields() ) {
            //--------------------------------------------------------------------------------------

                    field.setAccessible( true );

                //----------------------------------------------------------------------------------

                    String name = field.getName();

                //----------------------------------------------------------------------------------
                    if( !tag.hasKey( name ) ) continue;
                //----------------------------------------------------------------------------------

                    if( field.getType().isAssignableFrom( Integer.class ) )
                        try  { field.set( this , tag.getInteger( name ) ); }
                        catch( IllegalAccessException e ) { continue; }

                //----------------------------------------------------------------------------------

                    if( field.getType().isAssignableFrom( String.class ) )
                        try  { field.set( this , tag.getString( name ) ); }
                        catch( IllegalAccessException e ) { continue; }

                //----------------------------------------------------------------------------------

                    if( field.getType().isAssignableFrom( NBTTagCompound.class ) ) {
                        try  { field.set( this , tag.getCompoundTag( name ) ); }
                        catch( IllegalAccessException ex ) { continue; } }

            //--------------------------------------------------------------------------------------
                } clazz = clazz.getSuperclass(); }
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
        // Parser Utility
        //==========================================================================================

            Entry( @Nullable Entry other ) {
            //--------------------------------------------------------------------------------------
                if( null == other ) return;
            //--------------------------------------------------------------------------------------

                this.Mod   = other.Mod;
                this.Entry = other.Entry;
                this.Meta  = other.Meta;
                this.NBT   = other.NBT;

            //--------------------------------------------------------------------------------------
            }

        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
            /***************************************************************************************
             *
             *   Returns a list od item stacks that are related to this entry
             *
             ***************************************************************************************
             *
             *   - A test for a related item stack is
             *
             *      entry.Mod     <--->   stack.getItem().item.getRegistryName().getResourceDomain()
             *      entry.Entry   <--->   stack.getItem().getRegistryName().getResourcePath()
             *      entry.Meta    <--->   stack.getMetadata()
             *      entry.NBT     <--->   stack.getTagCompound()
             *
             *   - Entry properties that are null are not used in testing
             *
             ***************************************************************************************
             *
             *   @return a list of related items stacks
             *
             **************************************************************************************/
        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

            public List<ItemStack> related() {
            //--------------------------------------------------------------------------------------
                List<Item> items = new ArrayList<>( ForgeRegistries.ITEMS.getValues() );
            //--------------------------------------------------------------------------------------

                Function<Item , String> domain = s -> s.getRegistryName().getResourceDomain();
                Function<Item , String> path   = s -> s.getRegistryName().getResourcePath();

                if( null != Mod   ) items.removeIf( s -> !domain.apply( s ).equals( Mod   ) );
                if( null != Entry ) items.removeIf( s -> !path.apply  ( s ).equals( Entry ) );

            //--------------------------------------------------------------------------------------

                NonNullList<ItemStack> stacks = NonNullList.create();

            //--------------------------------------------------------------------------------------
                for( Item item : items ) {
            //--------------------------------------------------------------------------------------

                    CreativeTabs      tab = item.getCreativeTab();
                    if( null == tab ) tab = CreativeTabs.CREATIVE_TAB_ARRAY[0];

                    item.getSubItems( tab , stacks );

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------

                if( null != Meta ) stacks.removeIf( s -> !Meta.equals( s.getMetadata() ) );
                if( null != NBT  ) stacks.removeIf( s -> !s.hasTagCompound() );
                if( null != NBT  ) stacks.removeIf( s -> !s.getTagCompound().equals( NBT ) );

            //--------------------------------------------------------------------------------------
                return stacks;
            //--------------------------------------------------------------------------------------
            }

            public List<T> related( Collection<T> container ) {
            //--------------------------------------------------------------------------------------
                if( null != related ) return related;
            //--------------------------------------------------------------------------------------

                List<T> entries = new ArrayList<>( container );

            //--------------------------------------------------------------------------------------

                if( null != Mod   ) entries.removeIf( s -> !Mod.equals  ( s.Mod   ) );
                if( null != Entry ) entries.removeIf( s -> !Entry.equals( s.Entry ) );
                if( null != Meta  ) entries.removeIf( s -> !Meta.equals ( s.Meta  ) );
                if( null != NBT   ) entries.removeIf( s -> !NBT.equals  ( s.NBT   ) );

            //--------------------------------------------------------------------------------------

                related = entries;

            //--------------------------------------------------------------------------------------
                return related;
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            public ItemStack getRawItem() {
            //--------------------------------------------------------------------------------------

                ResourceLocation loc   = new ResourceLocation( Mod , Entry );
                ItemStack        stack = new ItemStack( Item.REGISTRY.getObject( loc ) , 1 , Meta );

            //--------------------------------------------------------------------------------------
                if( null != NBT ) try {
            //--------------------------------------------------------------------------------------

                    stack.setTagCompound( JsonToNBT.getTagFromJson( "" + this.NBT ) );

            //--------------------------------------------------------------------------------------
                } catch ( NBTException e ) { e.printStackTrace(); }
            //--------------------------------------------------------------------------------------

                return stack;

            //--------------------------------------------------------------------------------------
            }

            public IBakedModel getRawModel() {
            //--------------------------------------------------------------------------------------

                return Minecraft.getMinecraft().getRenderItem()
                                .getItemModelWithOverrides( this.getRawItem() , null , null );

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            public NBTTagCompound toTag() {
            //--------------------------------------------------------------------------------------
                NBTTagCompound tag = new NBTTagCompound();
            //--------------------------------------------------------------------------------------

                Class<?> clazz = this.getClass();

            //--------------------------------------------------------------------------------------
                while( clazz != null ) { for( Field field : clazz.getDeclaredFields() ) {
            //--------------------------------------------------------------------------------------

                    field.setAccessible( true );

                //----------------------------------------------------------------------------------
                    String name = field.getName();
                //----------------------------------------------------------------------------------

                    Object value;

                    try  { value = field.get( this ); }
                    catch( IllegalAccessException e ) { continue; }

                //----------------------------------------------------------------------------------

                    if( value instanceof     Integer    ) tag.setInteger( name , (Integer) value );
                    if( value instanceof     String     ) tag.setString ( name , (String)  value );
                    if( value instanceof NBTTagCompound ) tag.setTag(name , (NBTTagCompound) value);

            //--------------------------------------------------------------------------------------
                } clazz = clazz.getSuperclass(); } return tag;
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

        }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

        public static Map<Entry , Entry> entries = new LinkedHashMap<>();
        public static Map<Entry , Integer> textures = new LinkedHashMap<>();

    /*==============================================================================================
    // Parser section
    //============================================================================================*/

        /*******************************************************************************************
         *
         *   A YAML parser for modified YAML content.
         *
        ********************************************************************************************
         *
         *   Parses a file that has a bunch of entries that are related to items/blocks from other
         *   mods/vanilla manicraft.
         *
        ********************************************************************************************
         * FILE     - Starts with 3 dashes   ->   ---
         *          - Ends with 3 dots       ->   ...
         *          - Has a bunch of BLOCKs in between
         *          - Everything outside the body is ignored (Not parsed)
        ********************************************************************************************
         *
         *  ---
         *      BLOCK
         *       ···
         *      BLOCK
         *  ...
         *
        ********************************************************************************************
         * BLOCK    - Starts with 'Entries'
         *          - Can have a list of ENTRYs   ->   : ENTRY ··· ENTRY
         *          - Can have a default ENTRY    ->   [ ENTRY ]
        ********************************************************************************************
         *
         *      Entries: ENTRY ··· ENTRY
         *
         *      Entries[ ENTRY ]: ENTRY ··· ENTRY
         *
         *      Entries[ ENTRY ]
         *
        ********************************************************************************************
         * ENTRY    - a list of PROPERTYs
         *          - Starts with either a dash or an identifier
         *          - Properties are separated by either commas or whitespaces
        ********************************************************************************************
         *
         *      PROPERTY , ··· , PROPERTY
         *
         *      - PROPERTY ··· PROPERTY
         *
        ********************************************************************************************
         * PROPERTY     - either normal variable assignment or a dictionary entry assignment
        ********************************************************************************************
         *
         *      NAME = VALUE
         *      NAME : VALUE
         *
        ********************************************************************************************
        * NAME      - A valid java identifier
        ********************************************************************************************
         *
         *      Character.isJavaIdentifierStart( NAME[0] ) +
         *      Character.isJavaIdentifierPart ( NAME[1] ) +
         *                      ···
         *      Character.isJavaIdentifierPart ( NAME[N] ) +
         *
        ********************************************************************************************
        * VALUE     - Either an identifier, quoted text, a number, or JSON
        ********************************************************************************************
         *
         *      NAME
         *      " ··· "
         *      ' ··· '
         *      isDigit( VALUE[0] ) + ··· + isDigit( VALUE[N] )
         *      { ··· }
         *
        *******************************************************************************************/

        public static abstract class Parser {

        /*==========================================================================================
        // Utility
        //========================================================================================*/

            /***************************************************************************************
             *
             *   A java string pointer equivalent. So that methods can move the pointer without
             *   having to return another value.
             *
            ***************************************************************************************/

            public static class Content { String value; }

        /*==========================================================================================
        // Simple data types gobblers
        //========================================================================================*/

            /***************************************************************************************
             *
             *   Reads an integer from content and moves the content pointer to after the number.
             *
            ****************************************************************************************
             *
             *   Eats expressions like    82...92
             *
            ****************************************************************************************
             *
             *   @param content to be parsed (A java string pointer equivalent)
             *
             *   @return Integer       - if content.value starts with a number
             *         + content.value - set to content after the number
             *           null          - if content.value doesn't starts with a number
             *
            ***************************************************************************************/

            @Nullable public static Integer getInteger( Content content ) {
            //══════════════════════════════════════════════════════════════════════════════════════
            // Check if content has a number
            //══════════════════════════════════════════════════════════════════════════════════════
                if( content.value.isEmpty() ) return null;
            //--------------------------------------------------------------------------------------

                String reduced = StringUtils.trim( content.value );

            //--------------------------------------------------------------------------------------
                Character ch = reduced.charAt( 0 );
            //--------------------------------------------------------------------------------------

                if( !Character.isDigit( ch ) ) return null;

            //══════════════════════════════════════════════════════════════════════════════════════
            // Eat a number
            //══════════════════════════════════════════════════════════════════════════════════════
                Integer pos = 0; for( ; pos < reduced.length(); pos++ ) {
            //--------------------------------------------------------------------------------------

                    ch = reduced.charAt( pos );

                    if( !Character.isDigit( ch ) ) reduced = reduced.substring( pos );
                    if( !Character.isDigit( ch ) ) break;

            //--------------------------------------------------------------------------------------
                } if( pos == reduced.length() ) reduced = reduced.substring( pos );
            //══════════════════════════════════════════════════════════════════════════════════════
            // Extract and return a number
            //══════════════════════════════════════════════════════════════════════════════════════

                Integer end    = content.value.length() - reduced.length();
                Integer number = Integer.parseInt( content.value.substring( 0 , end ) );

            //--------------------------------------------------------------------------------------
                content.value = reduced; return number;
            //══════════════════════════════════════════════════════════════════════════════════════
            }

        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

            /***************************************************************************************
             *
             *   Reads quoted text or a word from content and moves the content pointer to after
             *   the text or word.
             *
            ****************************************************************************************
             *
             *   Eats expressions like   _k4r...6j
             *                           "k 4r...6\"j"
             *                           'k 4r...6\'j'
             *
            ****************************************************************************************
             *
             *   @param content to be parsed (A java string pointer equivalent)
             *
             *   @return String        - if content.value starts with a word or quoted text
             *         + content.value - set to content after the word/text
             *           null          - if content.value doesn't starts with a word or quoted text
             *
            ***************************************************************************************/

            @Nullable public static String getText( Content content ) {
            //══════════════════════════════════════════════════════════════════════════════════════
            // Check if content has a word or quoted text
            //══════════════════════════════════════════════════════════════════════════════════════
                if( content.value.isEmpty() ) return null;
            //--------------------------------------------------------------------------------------

                String reduced = StringUtils.trim( content.value );

            //--------------------------------------------------------------------------------------
                Boolean quoted = "\"'".contains( reduced.substring( 0 , 1 ) );
            //--------------------------------------------------------------------------------------

                Integer   depth = 1;
                Character quote = reduced.charAt( 0 );

                if( !quoted && !Character.isJavaIdentifierStart( reduced.charAt(0) ) ) return null;

            //══════════════════════════════════════════════════════════════════════════════════════
            // Eat a word
            //══════════════════════════════════════════════════════════════════════════════════════
                if( !quoted ) { Integer pos = 0; for( ; pos < reduced.length(); pos++ ) {
            //--------------------------------------------------------------------------------------

                    Character ch = reduced.charAt( pos );

                    if( !Character.isJavaIdentifierPart( ch ) ) reduced = reduced.substring( pos );
                    if( !Character.isJavaIdentifierPart( ch ) ) break;

            //--------------------------------------------------------------------------------------
                } if( pos == reduced.length() ) reduced = reduced.substring( pos );
            //══════════════════════════════════════════════════════════════════════════════════════
            // Eat quoted text
            //══════════════════════════════════════════════════════════════════════════════════════
                } else { Integer pos = 1; for( ; pos < reduced.length(); pos++ ) {
            //--------------------------------------------------------------------------------------

                    if( reduced.substring( pos ).startsWith( "\\\"" ) ) { pos++; continue; }
                    if( reduced.substring( pos ).startsWith( "\\\'" ) ) { pos++; continue; }

                    if( quote == reduced.charAt( pos ) ) depth--;

                    if( 0 == depth ) reduced = reduced.substring( pos + 1 );
                    if( 0 == depth ) break;

            //--------------------------------------------------------------------------------------
                } if( pos == reduced.length() ) reduced = reduced.substring( pos ); }
            //══════════════════════════════════════════════════════════════════════════════════════
            // Extract and return a word or quoted text
            //══════════════════════════════════════════════════════════════════════════════════════

                Integer end  = content.value.length() - reduced.length();
                String  text = content.value.substring( 0 , end );

                text = text.replaceAll( "\\\\\"" , "\"" );
                text = text.replaceAll( "\\\\'"  , "'"  );

            //--------------------------------------------------------------------------------------
                content.value = reduced; return text;
            //══════════════════════════════════════════════════════════════════════════════════════
            }

        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

            /***************************************************************************************
             *
             *   Reads JSON from content and moves the content pointer to after the JSON.
             *
            ****************************************************************************************
             *
             *   Eats expressions like   { ... }
             *
            ****************************************************************************************
             *
             *   @param content to be parsed (A java string pointer equivalent)
             *
             *   @return NBTTagCompound - if content.value starts with JSON
             *         + content.value  - set to content after the word/text
             *           null           - if content.value doesn't starts with JSON
             *
            ***************************************************************************************/

            @Nullable public static NBTTagCompound getJSON( Content content ) {
            //══════════════════════════════════════════════════════════════════════════════════════
            // Check if content has a JSON BLOCK
            //══════════════════════════════════════════════════════════════════════════════════════
                if( content.value.isEmpty() ) return null;
            //--------------------------------------------------------------------------------------

                String reduced = StringUtils.trim( content.value );

            //--------------------------------------------------------------------------------------
                if( !reduced.startsWith( "{" ) ) return null;
            //--------------------------------------------------------------------------------------

                Integer depth = 1;

            //══════════════════════════════════════════════════════════════════════════════════════
            // Eat a JSON BLOCK
            //══════════════════════════════════════════════════════════════════════════════════════
                Integer pos = 1; for( ; pos < reduced.length(); pos++ )  {
            //--------------------------------------------------------------------------------------

                    if( '{' == reduced.charAt( pos ) ) depth++;
                    if( '}' == reduced.charAt( pos ) ) depth--;

                    if( 0 == depth ) reduced = reduced.substring( pos + 1 );
                    if( 0 == depth ) break;

            //--------------------------------------------------------------------------------------
                } if( pos == reduced.length() ) reduced = reduced.substring( pos );
            //══════════════════════════════════════════════════════════════════════════════════════
            // Extract and return a JSON BLOCK
            //══════════════════════════════════════════════════════════════════════════════════════

                Integer     end  = content.value.length() - reduced.length();
                JsonElement json = new JsonParser().parse( content.value.substring( 0 , end ) );

            //--------------------------------------------------------------------------------------
                content.value = reduced;
            //--------------------------------------------------------------------------------------

                try   { return JsonToNBT.getTagFromJson( "" + json ); }
                catch ( NBTException ignored ) { return new NBTTagCompound(); }

            //══════════════════════════════════════════════════════════════════════════════════════
            }

        /*==========================================================================================
        // Complex data types gobblers
        //========================================================================================*/

            /***************************************************************************************
             *
             *   Reads a property from content, sets it on the container, and moves the content
             *   pointer to after the property.
             *
            ****************************************************************************************
             *
             *   Eats expressions like   Name = Value
             *                           Name : Value
             *
            ****************************************************************************************
             *
             *   @param content   to be parsed (A java string pointer equivalent)
             *   @param container to have it's property set
             *
             *   @out content.value   - set to content after the property
             *   @out container.field - set to property value if it equals property name
             *
            ***************************************************************************************/

            public static <K> boolean setProperty( Content content , K container ) {
            //══════════════════════════════════════════════════════════════════════════════════════
            // Check if content properly starts
            //══════════════════════════════════════════════════════════════════════════════════════

                content.value = StringUtils.trim( content.value );

                if( content.value.isEmpty() ) return false;
                if( content.value.startsWith( "Entries" ) ) return false;

            //--------------------------------------------------------------------------------------

                if( !Character.isLetter( content.value.charAt( 0 ) ) ) return false;

            //══════════════════════════════════════════════════════════════════════════════════════
            // Eat a property name
            //══════════════════════════════════════════════════════════════════════════════════════

                String name = getText( content );

            //--------------------------------------------------------------------------------------
                content.value = StringUtils.trim( content.value );
            //══════════════════════════════════════════════════════════════════════════════════════
            // Eat a property delimiter
            //══════════════════════════════════════════════════════════════════════════════════════

                if( !":=".contains( content.value.substring( 0 , 1 ) ) ) return false;

            //--------------------------------------------------------------------------------------
                content.value = StringUtils.trim( content.value.substring( 1 ) );
            //══════════════════════════════════════════════════════════════════════════════════════
            // Eat a property value
            //══════════════════════════════════════════════════════════════════════════════════════

                String         text   = getText   ( content );
                NBTTagCompound json   = getJSON   ( content );
                Integer        number = getInteger( content );

            //--------------------------------------------------------------------------------------
                content.value = StringUtils.trim( content.value );
            //══════════════════════════════════════════════════════════════════════════════════════
            // Set a property on the container
            //══════════════════════════════════════════════════════════════════════════════════════

                Class<?> clazz = container.getClass();

            //--------------------------------------------------------------------------------------
                while( clazz != null ) { try {
            //--------------------------------------------------------------------------------------

                    Field field = clazz.getDeclaredField( name );

                //----------------------------------------------------------------------------------
                    field.setAccessible( true );
                //----------------------------------------------------------------------------------

                    if( null != number ) field.set( container , number );
                    if( null != text   ) field.set( container , text   );
                    if( null != json   ) field.set( container , json   );

                    break;

            //--------------------------------------------------------------------------------------
                } catch ( NoSuchFieldException   ex ) { clazz = clazz.getSuperclass(); }
            //--------------------------------------------------------------------------------------
                  catch ( IllegalAccessException ex ) { } } return true;
            //══════════════════════════════════════════════════════════════════════════════════════
            }

        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

            /***************************************************************************************
             *
             *   Reads a bunch of properties from the content, sets them on the container, and moves
             *   the content pointer to after all the the properties.
             *
            ****************************************************************************************
             *
             *   Eats expressions like     Name = Value , ... , Name = Value
             *                           - Name : Value   ...   Name : Value
             *
            ****************************************************************************************
             *
             *   @param content   to be parsed (A java string pointer equivalent)
             *   @param container to have it's properties set
             *
             *   @out content.value   - set to content after the property
             *   @out container.field - set to property value if it equals property name
             *
            ***************************************************************************************/

            public static <K> boolean setEntry( Content content , K container ) {
            //══════════════════════════════════════════════════════════════════════════════════════
            // Check if content properly starts
            //══════════════════════════════════════════════════════════════════════════════════════

                content.value = StringUtils.trim( content.value );

                if( content.value.isEmpty() ) return false;
                if( content.value.startsWith( "Entries" ) ) return false;

            //--------------------------------------------------------------------------------------

                Boolean word = Character.isJavaIdentifierStart( content.value.charAt( 0 ) );
                Boolean dash = '-' == content.value.charAt( 0 );

                if( !word && !dash ) return false;

                if( dash ) content.value = StringUtils.trim( content.value.substring( 1 ) );

            //══════════════════════════════════════════════════════════════════════════════════════
            // Eat all the properties (That aren't blocked)
            //══════════════════════════════════════════════════════════════════════════════════════
                while( !content.value.isEmpty() ) {
            //--------------------------------------------------------------------------------------

                    setProperty( content , container );

                //----------------------------------------------------------------------------------
                    if( content.value.isEmpty() ) break;
                //══════════════════════════════════════════════════════════════════════════════════
                // Eat properties deliminator
                //══════════════════════════════════════════════════════════════════════════════════

                    Boolean letter = Character.isLetter( content.value.charAt( 0 ) );
                    Boolean comma  = ",".contains( content.value.substring( 0 , 1 ) );

                //----------------------------------------------------------------------------------
                    if( !letter && !comma ) break;
                //----------------------------------------------------------------------------------

                    if( comma  ) content.value = content.value.substring( 1 );
                    if( letter ) if( content.value.startsWith( "Entries" ) ) break;

            //--------------------------------------------------------------------------------------
                } return true;
            //══════════════════════════════════════════════════════════════════════════════════════
            }

        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

            /***************************************************************************************
             *
             *   Reads a BLOCK of entries, creates a bunch of containers that corresponds to those
             *   entries, and moves the content pointer to after the BLOCK.
             *
            ****************************************************************************************
             *
             *   Eats expressions like   Entries[ Entry ]
             *                           Entries: Entry ... Entry
             *                           Entries[ Entry ]: Entry ... Entry
             *
            ****************************************************************************************
             *
             *   @param content to be parsed (A java string pointer equivalent)
             *
             *   @out content.value - set to content after all the entries in this entries BLOCK
             *
             *   @return a list of filled out containers
             *
            ***************************************************************************************/

            public static <K> List<K> getEntries( Content content , Class<K> clazz ) {
            //══════════════════════════════════════════════════════════════════════════════════════
            // Check if content properly starts
            //══════════════════════════════════════════════════════════════════════════════════════

                content.value = StringUtils.trim( content.value );

                if( content.value.isEmpty() ) return new ArrayList<>();
                if( !content.value.startsWith( "Entries" ) ) return new ArrayList<>();

            //══════════════════════════════════════════════════════════════════════════════════════
            // Eat the keyword
            //══════════════════════════════════════════════════════════════════════════════════════

                content.value = StringUtils.removeStart( content.value , "Entries" );

            //--------------------------------------------------------------------------------------
                content.value = StringUtils.trim( content.value );
            //══════════════════════════════════════════════════════════════════════════════════════
            // Eat the default entry (If it exists)
            //══════════════════════════════════════════════════════════════════════════════════════

                Character start = content.value.charAt( 0 );

            //--------------------------------------------------------------------------------------
                List<K> list = new ArrayList<>();
            //--------------------------------------------------------------------------------------

                K def = null;

            //--------------------------------------------------------------------------------------
                if( '[' == start ) {
            //--------------------------------------------------------------------------------------

                    content.value = content.value.substring( 1 );

                //----------------------------------------------------------------------------------

                    try  { def = clazz.newInstance(); }
                    catch( InstantiationException | IllegalAccessException e ) { return list; }

                //----------------------------------------------------------------------------------

                    if( null != def ) setEntry( content , def );

                //----------------------------------------------------------------------------------
                    content.value = StringUtils.trim( content.value );
                //----------------------------------------------------------------------------------

                    if( content.value.startsWith("]") ) content.value = content.value.substring(1);

            //--------------------------------------------------------------------------------------
                }
            //══════════════════════════════════════════════════════════════════════════════════════
            // Process if there are any more entries
            //══════════════════════════════════════════════════════════════════════════════════════

                if( !content.value.startsWith( ":" ) ) return Lists.newArrayList( def );

            //--------------------------------------------------------------------------------------
                content.value = content.value.substring( 1 );
            //══════════════════════════════════════════════════════════════════════════════════════
            // Eat all the entries
            //══════════════════════════════════════════════════════════════════════════════════════
                while( !content.value.isEmpty() ) {
            //--------------------------------------------------------------------------------------

                    K entry;

                //----------------------------------------------------------------------------------

                    try  { entry = clazz.getDeclaredConstructor( clazz ).newInstance( def ); }
                    catch( Exception ex) { return list; }

                //----------------------------------------------------------------------------------

                    if( !setEntry( content , entry ) ) return list;

                //----------------------------------------------------------------------------------
                    content.value = StringUtils.trim( content.value );
                //----------------------------------------------------------------------------------

                    list.add( entry );

            //--------------------------------------------------------------------------------------
                } return list;
            //══════════════════════════════════════════════════════════════════════════════════════
            }

        /*==========================================================================================
        // Usage
        //========================================================================================*/

            /***************************************************************************************
             *
             *   Parses a modified YAMl file returning a list of items corresponding to entries
             *   from that file.
             *
            ****************************************************************************************
             *
             *   @param file to be parsed
             *   @param clazz a class that corresponds to an entry from the file
             *
             *   @return a list of filled out clazz instances
             *
            ***************************************************************************************/

            public static <K> Set<K> Parse( File file , Class<K> clazz ) { try {
            //--------------------------------------------------------------------------------------

                String[] lines = FileUtils.readFileToString( file , "utf8" ).split( "\n" );

                for( int i = 0; i < lines.length; i++ ) { lines[i] = " " + lines[i];
                                                          lines[i] = lines[i].split( "#" )[0];
                                                          lines[i] = StringUtils.trim( lines[i] );
                                                          lines[i] = lines[i] + " "; }

            //--------------------------------------------------------------------------------------
                Content content = new Content();
            //--------------------------------------------------------------------------------------

                content.value = String.join( "", lines ).split( "---" )[1].split( "\\.\\.\\." )[0];

            //--------------------------------------------------------------------------------------

                Set<K> entries = new LinkedHashSet<>();

            //--------------------------------------------------------------------------------------
                while( !content.value.isEmpty() ) {
            //--------------------------------------------------------------------------------------

                    content.value = content.value + " ";

                    List<K> values = getEntries( content , clazz );

                //----------------------------------------------------------------------------------
                    if( values.isEmpty() ) break;
                //----------------------------------------------------------------------------------

                    entries.addAll( values );

            //--------------------------------------------------------------------------------------
                } return entries;
            //--------------------------------------------------------------------------------------
            } catch( IOException ex ) { ex.printStackTrace(); } return new LinkedHashSet<>(); }

        //==========================================================================================

        }

    /*==============================================================================================
    // Blocks section
    //============================================================================================*/

        /*******************************************************************************************
         *
         *   Represents a dynamic BLOCK with a tile entity that has been placed in the world.
         *
         *******************************************************************************************
         *
         *   This BLOCK is special in that it behaves and looks differently based on JSON data
         *   (NBT tags specifically). In essence it "emulates" other blocks.
         *
         *   The reason for this is that minecraft doesn't allow addition and removal of blocks
         *   during gameplay. A dynamic BLOCK is a way around that restriction.
         *
         ******************************************************************************************/

        public static abstract class Block extends net.minecraft.block.Block {
        //==========================================================================================

            public Block( Material materialIn ) {
            //--------------------------------------------------------------------------------------
                super( materialIn );
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public boolean isOpaqueCube( IBlockState state ) {
            //--------------------------------------------------------------------------------------
                return false;
            //--------------------------------------------------------------------------------------
            }

            @Override @SideOnly( Side.CLIENT ) public BlockRenderLayer getBlockLayer() {
            //--------------------------------------------------------------------------------------
                return BlockRenderLayer.TRANSLUCENT;
            //--------------------------------------------------------------------------------------
            }

            @Override public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
            //--------------------------------------------------------------------------------------
                if( BlockRenderLayer.TRANSLUCENT == layer ) return true;

                return false;
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public boolean hasTileEntity( IBlockState state ) {
            //--------------------------------------------------------------------------------------
                return true;
            //--------------------------------------------------------------------------------------
            }

            @Override @Nullable public net.minecraft.tileentity.TileEntity createTileEntity(
            //--------------------------------------------------------------------------------------
                    World world ,
                    IBlockState state
            //--------------------------------------------------------------------------------------
            ) {
            //--------------------------------------------------------------------------------------
                return new TileEntity();
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
        }

    //==============================================================================================

        public static class TileEntity extends net.minecraft.tileentity.TileEntity {

        //==========================================================================================

        //==========================================================================================

        }

    /*==============================================================================================
    // Items section
    //============================================================================================*/

        /*******************************************************************************************
         *
         *   Represents a dynamic item that can be placed as a dynamic BLOCK in the world.
         *
         *******************************************************************************************
         *
         *   This item is special in that it behaves and looks differently based on JSON data
         *   (NBT tags specifically). In essence it "emulates" other items.
         *
         *   The reason for this is that minecraft doesn't allow addition and removal of items
         *   during gameplay. A dynamic item is a way around that restriction.
         *
         ******************************************************************************************/

        public static abstract class ItemBlock extends net.minecraft.item.ItemBlock {
        //==========================================================================================

            public ItemBlock( Block block ) {
            //--------------------------------------------------------------------------------------
                super( block );
            //--------------------------------------------------------------------------------------

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
        }

    /*==============================================================================================
    // Model section
    //============================================================================================*/

        public static abstract class Overrides extends ItemOverrideList {

        //==========================================================================================

            public Overrides() {
            //--------------------------------------------------------------------------------------
                super( new ArrayList<>() );
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

        }

        public static abstract class BakedModel implements IBakedModel {

        //==========================================================================================

            public Entry     parent;
            public Overrides overrides;

        //==========================================================================================

            protected TransformType transform;

        //==========================================================================================

            @Override public ItemOverrideList getOverrides() {
            //--------------------------------------------------------------------------------------
                return this.overrides;
            //--------------------------------------------------------------------------------------
            }

            @Override public Pair<IBakedModel, Matrix4f> handlePerspective(TransformType type){
            //--------------------------------------------------------------------------------------
                Matrix4f mat = new Matrix4f();
            //--------------------------------------------------------------------------------------

                mat.setIdentity();

            //--------------------------------------------------------------------------------------
                transform = type; return new ImmutablePair<>( this , mat );
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public boolean isAmbientOcclusion() {
            //--------------------------------------------------------------------------------------
                return false;
            //--------------------------------------------------------------------------------------
            }

            @Override public boolean isGui3d() {
            //--------------------------------------------------------------------------------------
                return true;
            //--------------------------------------------------------------------------------------
            }

            @Override public boolean isBuiltInRenderer() {
            //--------------------------------------------------------------------------------------
                return false;
            //--------------------------------------------------------------------------------------
            }

            @Override public TextureAtlasSprite getParticleTexture() {
            //--------------------------------------------------------------------------------------
                if( null != parent ) return parent.getRawModel().getParticleTexture();

                return null;
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

        }

        public static abstract class Model implements IModel {

        //==========================================================================================

            @Override
            public IBakedModel bake(
                    IModelState state,
                    VertexFormat format,
                    Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
                return null;
            }

        //==========================================================================================

        }

        public static abstract class CustomModelLoader implements ICustomModelLoader {

        //==========================================================================================

            @Override public boolean accepts( ResourceLocation modelLocation ) {
            //--------------------------------------------------------------------------------------

                return modelLocation.getResourceDomain().equals( Base.modId );

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public IModel loadModel(ResourceLocation modelLocation) throws Exception {
            //--------------------------------------------------------------------------------------

                return new Model() {};

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public void onResourceManagerReload( IResourceManager resourceManager ) {
            //--------------------------------------------------------------------------------------

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

        }

    /*==============================================================================================
    // Rendering section
    //============================================================================================*/

        public static abstract class Renderer<T extends TileEntity>
                                                extends TileEntitySpecialRenderer<T> {

        //==========================================================================================

            public static Integer defTexture;

        //==========================================================================================

            public static Boolean started = false;

        //==========================================================================================

            public static Boolean      draw;
            public static Integer      mode;
            public static VertexFormat type;

        //==========================================================================================

            public static void Start() {
            //--------------------------------------------------------------------------------------
                if( started ) return; started = true;
            //--------------------------------------------------------------------------------------

            /*    BufferBuilder        buffer = Tessellator.getInstance().getBuffer();
                Class<BufferBuilder> clazz  = BufferBuilder.class;

                draw = ReflectionHelper.getPrivateValue( clazz , buffer, "isDrawing"    );
                mode = ReflectionHelper.getPrivateValue( clazz , buffer, "drawMode"     );
                type = ReflectionHelper.getPrivateValue( clazz , buffer, "vertexFormat" );

                if( draw ) buffer.finishDrawing();//*/

            //--------------------------------------------------------------------------------------

                GL11.glPushAttrib( GL11.GL_ALL_ATTRIB_BITS );

                GL11.glMatrixMode( GL11.GL_COLOR );
                GL11.glPushMatrix();

                GL11.glMatrixMode( GL11.GL_TEXTURE );
                GL11.glPushMatrix();

                GL11.glMatrixMode( GL11.GL_PROJECTION );
                GL11.glPushMatrix();

                GL11.glMatrixMode( GL11.GL_MODELVIEW );
                GL11.glPushMatrix();

            //--------------------------------------------------------------------------------------

                GlStateManager.enableCull();

                GlStateManager.enableDepth();
                GlStateManager.depthMask( true );

                GlStateManager.enableBlend();
                GlStateManager.blendFunc( GlStateManager.SourceFactor.SRC_ALPHA,
                                          GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA );

                GlStateManager.enableTexture2D();

            //--------------------------------------------------------------------------------------
            }

            public static void End( Boolean inWorld ) {
            //--------------------------------------------------------------------------------------
                if( !started ) return;
            //--------------------------------------------------------------------------------------

                if( inWorld ) GlStateManager.disableBlend();

            //--------------------------------------------------------------------------------------

                GL11.glPopAttrib();

                GL11.glMatrixMode( GL11.GL_COLOR );
                GL11.glPopMatrix();

                GL11.glMatrixMode( GL11.GL_TEXTURE );
                GL11.glPopMatrix();

                GL11.glMatrixMode( GL11.GL_PROJECTION );
                GL11.glPopMatrix();

                GL11.glMatrixMode( GL11.GL_MODELVIEW );
                GL11.glPopMatrix();

            //--------------------------------------------------------------------------------------

            /*    BufferBuilder buffer = Tessellator.getInstance().getBuffer();

                if( draw ) buffer.begin( mode , type );//*/

            //--------------------------------------------------------------------------------------
                started = false;
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            public static void bindDefTex() {
            //--------------------------------------------------------------------------------------

                TextureManager manager = Minecraft.getMinecraft().getTextureManager();
                ITextureObject defTex  = manager.getTexture( TextureMap.LOCATION_BLOCKS_TEXTURE );

            //--------------------------------------------------------------------------------------

                GL11.glBindTexture( GL11.GL_TEXTURE_2D , defTex.getGlTextureId() );

            //--------------------------------------------------------------------------------------
            }

            public static void bindTex( @Nullable Entry entry ) {
            //--------------------------------------------------------------------------------------
                Integer texture = textures.getOrDefault( entry , defTexture );
            //--------------------------------------------------------------------------------------

                if( null != texture ) GL11.glBindTexture( GL11.GL_TEXTURE_2D , texture );

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            public static void renderRaw( @Nullable Entry entry ) {
            //--------------------------------------------------------------------------------------

                if( null == entry ) return;

            //--------------------------------------------------------------------------------------
                bindTex( entry );
            //--------------------------------------------------------------------------------------

                Minecraft.getMinecraft().getRenderItem().renderItem( entry.getRawItem()
                                                                   , entry.getRawModel() );

            //--------------------------------------------------------------------------------------
               GlStateManager.disableRescaleNormal();
            //--------------------------------------------------------------------------------------

                Integer texture = GlStateManager.glGetInteger( GL11.GL_TEXTURE_BINDING_2D );

                if( texture != defTexture ) textures.putIfAbsent( entry , texture );

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

        }

    /*==============================================================================================
    // Recipes section
    //============================================================================================*/

        public static abstract class ShapelessRecipe extends ShapelessRecipes {

        //==========================================================================================

            public ShapelessRecipe(
            //--------------------------------------------------------------------------------------
                  String group
                , ItemStack output
                , NonNullList<Ingredient> ingredients
            //--------------------------------------------------------------------------------------
            ) {
            //--------------------------------------------------------------------------------------
                super( group , output , ingredients );
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Nullable public ItemStack getGridStack( InventoryCrafting grid ) {

                Integer h = grid.getHeight();
                Integer w = grid.getWidth();

            //--------------------------------------------------------------------------------------

                List<ItemStack> contents = new ArrayList<>();

                for( int i = 0; i < h * w; i++ ) contents.add( grid.getStackInSlot( i ) );

            //--------------------------------------------------------------------------------------

                Integer count = 0;

                for( int i = 0; i < h * w; i++ ) if( !contents.get( i ).isEmpty() ) count++;

            //--------------------------------------------------------------------------------------
                ItemStack stack = contents.stream().reduce( ItemStack.EMPTY , ( s , t ) -> {
            //--------------------------------------------------------------------------------------

                    if( null == s ) return null;

                //----------------------------------------------------------------------------------

                    if( s.isEmpty() ) return t;
                    if( t.isEmpty() ) return s;

                //----------------------------------------------------------------------------------

                    Entry entryS = new Compressed.Entry( s );
                    Entry entryT = new Compressed.Entry( t );

                    if( entryS.equals( entryT ) ) return t;

                //----------------------------------------------------------------------------------

                    return null;

            //--------------------------------------------------------------------------------------
                } ).copy();
            //--------------------------------------------------------------------------------------

                stack.setCount( count );

            //--------------------------------------------------------------------------------------
                return stack;
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

        }

    //==============================================================================================

    }

//==================================================================================================
