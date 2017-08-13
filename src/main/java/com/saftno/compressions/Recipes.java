//==============================================================================================

    package com.saftno.compressions;

//==============================================================================================

    import com.saftno.compressions.Configurations.Entry;
    import com.saftno.compressions.ItemBlocks.Compressed.ItemX;
    import com.saftno.compressions.ResourcePacks.Type;

//==============================================================================================

    import com.google.gson.JsonObject;
    import com.google.gson.JsonParser;
    import net.minecraft.init.Items;
    import net.minecraft.inventory.InventoryCrafting;
    import net.minecraft.item.ItemStack;
    import net.minecraft.item.crafting.IRecipe;
    import net.minecraft.item.crafting.ShapelessRecipes;
    import net.minecraft.nbt.JsonToNBT;
    import net.minecraft.nbt.NBTException;
    import net.minecraft.nbt.NBTTagCompound;
    import net.minecraftforge.common.crafting.CraftingHelper;
    import net.minecraftforge.common.crafting.JsonContext;
    import net.minecraftforge.fml.common.registry.ForgeRegistries;
    import org.apache.commons.io.IOUtils;
    import org.apache.commons.lang3.tuple.ImmutablePair;
    import org.apache.commons.lang3.tuple.Pair;

//==============================================================================================

    import java.io.IOException;
    import java.io.InputStream;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.HashSet;
    import java.util.Map;
    import java.util.Set;

//==============================================================================================

    public class Recipes {

    //==========================================================================================
    // Structure
    //==========================================================================================

        public static class Compressed extends ShapelessRecipes {

        //======================================================================================
        // Structure
        //======================================================================================

            public static Map<String , ArrayList<Compressed>> connected = new HashMap<>();

        //======================================================================================

            String tags;

        //======================================================================================
        // Unique identification
        //======================================================================================

            @Override public boolean equals( Object object ) {
            //----------------------------------------------------------------------------------
                if( !( object instanceof Compressed ) ) return false;
            //----------------------------------------------------------------------------------

                Compressed other = (Compressed) object;

            //----------------------------------------------------------------------------------

                String regNameThis  = this.getRegistryName().toString();
                String regNameOther = other.getRegistryName().toString();

                if( !regNameThis.equals( regNameOther ) ) return false;

            //----------------------------------------------------------------------------------
                return true;
            //----------------------------------------------------------------------------------
            }

            @Override public int hashCode() {
            //---------------------------------------------------------------------------------

                return this.getRegistryName().toString().hashCode();

            //---------------------------------------------------------------------------------
            }

        //======================================================================================
        // Usage
        //======================================================================================

            public Compressed( JsonObject content , JsonContext context ) {
            //----------------------------------------------------------------------------------
                super(  CraftingHelper.getRecipe( content , context ).getGroup()        ,
                        CraftingHelper.getRecipe( content , context ).getRecipeOutput() ,
                        CraftingHelper.getRecipe( content , context ).getIngredients()  ); try {
            //----------------------------------------------------------------------------------

                ItemStack in = getIngredients().get( 0 ).getMatchingStacks()[0];

            //----------------------------------------------------------------------------------

                tags = in.getItem().getRegistryName().toString() + in.getMetadata();

            //----------------------------------------------------------------------------------
                if( !connected.containsKey( tags ) ) connected.put( tags , new ArrayList<>());
            //----------------------------------------------------------------------------------

                connected.get( tags ).add( this );

            //----------------------------------------------------------------------------------
                for( int i = 0; i < this.getIngredients().size(); i++ ) {
            //----------------------------------------------------------------------------------

                    NBTTagCompound tag = JsonToNBT.getTagFromJson( content.get( "ingredients" )
                                                  .getAsJsonArray()
                                                  .get( i )
                                                  .getAsJsonObject()
                                                  .get( "nbt" )
                                                  .toString() );

                    this.getIngredients().get( i ).getMatchingStacks()[0].setTagCompound( tag );

            //----------------------------------------------------------------------------------
            } } catch( NBTException ex ) { ex.printStackTrace(); } }

        //======================================================================================
        // Need this override to fix the NBT tag issues
        //======================================================================================

            @Override public ItemStack getCraftingResult( InventoryCrafting inv ) {
            //──────────────────────────────────────────────────────────────────────────────────
                if( 1 == this.getIngredients().size() ) super.getCraftingResult( inv );
            //──────────────────────────────────────────────────────────────────────────────────

                ItemStack stackThis = this.getIngredients().get( 0 ).getMatchingStacks()[0];
                ItemStack stackInv  = inv.getStackInSlot( 0 );

            //----------------------------------------------------------------------------------

                if( stackInv.getItem().equals( Items.AIR ) )
                    for( int i = 1; i < 9; i++ )
                        if( !inv.getStackInSlot( i ).getItem().equals( Items.AIR ) )
                            stackInv = inv.getStackInSlot( i );

            //----------------------------------------------------------------------------------

                if( !stackInv.hasTagCompound() )
                    return super.getCraftingResult( inv );

                if( stackInv.getTagCompound().toString().equals( "{}" ) )
                    return super.getCraftingResult( inv );

            //──────────────────────────────────────────────────────────────────────────────────

                String tagInv  = stackInv.getTagCompound().toString();
                String tagThis = stackThis.getTagCompound().toString();

            //----------------------------------------------------------------------------------

                if( tagInv.equals( tagThis ) ) return super.getCraftingResult( inv );

            //----------------------------------------------------------------------------------
                for( Compressed recipe : connected.get( this.tags ) ) {
            //----------------------------------------------------------------------------------

                    if( recipe.equals( this ) ) continue;

                //------------------------------------------------------------------------------

                    ItemStack stackAlt = recipe.getIngredients().get(0).getMatchingStacks()[0];
                    String    tagAlt   = stackAlt.getTagCompound().toString();

                //------------------------------------------------------------------------------

                    if( tagInv.equals( tagAlt ) ) return recipe.getCraftingResult( inv );

            //----------------------------------------------------------------------------------
                }
            //──────────────────────────────────────────────────────────────────────────────────

                return new ItemStack( Items.AIR , 1 , 0 );

            //----------------------------------------------------------------------------------
            }

        //======================================================================================

        }

    //==========================================================================================

        public static Set<Compressed> entries = new HashSet<>();

    //==========================================================================================
    // Setup
    //==========================================================================================

        public static void Register() {
        //--------------------------------------------------------------------------------------

            Generate();

        //--------------------------------------------------------------------------------------

            for( IRecipe entry : entries ) ForgeRegistries.RECIPES.register( entry );

        //--------------------------------------------------------------------------------------
        }

    //==========================================================================================

        public static void Generate() {  try {
        //──────────────────────────────────────────────────────────────────────────────────────
            for( ItemBlocks.Compressed compressed : ItemBlocks.entries ) {
        //--------------------------------------------------------------------------------------

                Entry entry = compressed.getEntry();

            //----------------------------------------------------------------------------------
                for( ItemX item : compressed.items ) {
            //----------------------------------------------------------------------------------

                    String coName = "co_" + item.getRegistryName().getResourcePath();
                    String deName = "de_" + item.getRegistryName().getResourcePath();

                //------------------------------------------------------------------------------

                    Pair<String, String> newRecipes = GenerateJSON( entry , item );

                    String coNew = newRecipes.getLeft();
                    String deNew = newRecipes.getRight();

                    JsonObject coNewJSON = new JsonParser().parse( coNew ).getAsJsonObject();
                    JsonObject deNewJSON = new JsonParser().parse( deNew ).getAsJsonObject();

                //------------------------------------------------------------------------------

                    Path coPath = ResourcePacks.Path( Type.RECIPE , coName );
                    Path dePath = ResourcePacks.Path( Type.RECIPE , deName );

                    InputStream coIn = Files.exists(coPath)? Files.newInputStream(coPath): null;
                    InputStream deIn = Files.exists(dePath)? Files.newInputStream(dePath): null;

                    String coOld = null == coIn ? "{}" : IOUtils.toString( coIn , "utf8" );
                    String deOld = null == deIn ? "{}" : IOUtils.toString( deIn , "utf8" );

                    JsonObject coOldJSON = new JsonParser().parse( coOld ).getAsJsonObject();
                    JsonObject deOldJSON = new JsonParser().parse( deOld ).getAsJsonObject();

                //------------------------------------------------------------------------------

                    Type type = Type.RECIPE;

                    if( !coNewJSON.equals(coOldJSON) ) ResourcePacks.Write(coNew, type, coName);
                    if( !deNewJSON.equals(deOldJSON) ) ResourcePacks.Write(deNew, type, deName);

                //------------------------------------------------------------------------------

                    JsonContext ctx = new JsonContext( "minecraft" );

                    Compressed coR = new Compressed( coNewJSON , ctx );
                    Compressed deR = new Compressed( deNewJSON , ctx );

                    coR.setRegistryName( Base.modId , coName );
                    deR.setRegistryName( Base.modId , deName );

                    entries.add( coR );
                    entries.add( deR );

                //------------------------------------------------------------------------------

                    entry.Mod   = item.getRegistryName().getResourceDomain();
                    entry.Entry = item.getRegistryName().getResourcePath();
                    entry.Meta  = 0;
                    entry.NBT = "{}";

        //--------------------------------------------------------------------------------------
            } }
        //──────────────────────────────────────────────────────────────────────────────────────
        } catch( IOException ex ) { ex.printStackTrace(); } }

    //==========================================================================================
    // Helpers
    //==========================================================================================

        public static Pair<String, String> GenerateJSON( Entry entry , ItemX item ) {
        //──────────────────────────────────────────────────────────────────────────────────────
            final String ingredientT = String.join( "\n" , new String[] {
        //--------------------------------------------------------------------------------------

                    "{ 'item' :  '[MOD]:[ENTRY]' ",
                    "[S]  , 'data' :   [META]         ",
                    "[S]  , 'count':   [COUNT]        ",
                    "[S]  , 'nbt'  :   [NBT] }        "

        //--------------------------------------------------------------------------------------
            } ); final String recipeT = String.join( "\n" , new String[] {
        //--------------------------------------------------------------------------------------

                    "{ 'type'       : 'crafting_shapeless'           " ,
                    ", 'group'      : '[GROUP]'                      " ,
                    ", 'ingredients': [ 0- 1- 2- 3- 4- 5- 6- 7- 8- ] " ,
                    ", 'result'     :  [RESULT] }                    " ,

        //--------------------------------------------------------------------------------------
            } );
        //──────────────────────────────────────────────────────────────────────────────────────

            String modS   = "" + ( null == entry.Mod   ? "''" : entry.Mod   );
            String entryS = "" + ( null == entry.Entry ? "''" : entry.Entry );
            String metaS  = "" + ( null == entry.Meta  ? "0"  : entry.Meta  );
            String nbtS   = "" + ( ""   == entry.NBT   ? "{}" : entry.NBT   );

            nbtS = nbtS.replaceAll( "\"" , " " );
            nbtS = nbtS.replaceAll( "(\\w+[:]*\\w+)" , "\"$1\"" );

        //--------------------------------------------------------------------------------------

            String ingredientBot = ingredientT.replace( "[MOD]"   , modS   )
                                              .replace( "[ENTRY]" , entryS )
                                              .replace( "[META]"  , metaS  )
                                              .replace( "[NBT]"   , nbtS   );

        //--------------------------------------------------------------------------------------

            modS   = item.getRegistryName().getResourceDomain();
            entryS = item.getRegistryName().getResourcePath();
            metaS  = "0";
            nbtS   = "{}";

        //--------------------------------------------------------------------------------------

            String ingredientTop = ingredientT.replace( "[MOD]"   , modS   )
                                              .replace( "[ENTRY]" , entryS )
                                              .replace( "[META]"  , metaS  )
                                              .replace( "[NBT]"   , nbtS   );

        //──────────────────────────────────────────────────────────────────────────────────────

            String co = recipeT.replace( "[GROUP]" , "compressing"   );
            String de = recipeT.replace( "[GROUP]" , "decompressing" );

        //--------------------------------------------------------------------------------------
            for( int i = 0; i < 9; i++ ) {
        //--------------------------------------------------------------------------------------

                if( i < entry.Width && i != entry.Width - 1 )
                    co = co.replace( i + "-" , ingredientBot + ", \n[S]" );

                if( i < entry.Width && i == entry.Width - 1 )
                    co = co.replace( i + "-" , ingredientBot );

            //----------------------------------------------------------------------------------

                if( i >= entry.Width ) co = co.replace( i + "-" , "" );

                if( 0 == i ) de = de.replace( i + "-" , ingredientTop );
                if( 0 != i ) de = de.replace( i + "-" , ""            );

        //--------------------------------------------------------------------------------------
            }
        //--------------------------------------------------------------------------------------

            co = co.replace( "[S]"      , "                 " );
            co = co.replace( "[COUNT]"  , "1"                 );
            co = co.replace( "[RESULT]" , ingredientTop       );
            co = co.replace( "[S]"      , "                 " );
            co = co.replace( "[COUNT]"  , "1"                 );

            de = de.replace( "[S]"      , "                  " );
            de = de.replace( "[COUNT]"  , "1"                  );
            de = de.replace( "[RESULT]" , ingredientBot        );
            de = de.replace( "[S]"      , "                  " );
            de = de.replace( "[COUNT]"  , "" + entry.Width     );

            return new ImmutablePair<>( co , de );

        //──────────────────────────────────────────────────────────────────────────────────────
        }

    //==========================================================================================

    }

//==============================================================================================
