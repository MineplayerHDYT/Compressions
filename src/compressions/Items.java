//==================================================================================================

    package compressions;

//==================================================================================================

    import mcp.MethodsReturnNonnullByDefault;
    import net.minecraft.block.Block;
    import net.minecraft.block.material.Material;
    import net.minecraft.creativetab.CreativeTabs;
    import net.minecraft.item.Item;
    import net.minecraft.item.ItemBlock;
    import net.minecraft.item.ItemStack;
    import net.minecraft.nbt.JsonToNBT;
    import net.minecraft.nbt.NBTException;
    import net.minecraft.nbt.NBTTagCompound;
    import net.minecraft.tileentity.TileEntityFurnace;
    import net.minecraft.util.NonNullList;
    import net.minecraft.util.ResourceLocation;
    import net.minecraftforge.event.RegistryEvent.Register;
    import net.minecraftforge.fml.common.Mod;
    import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
    import net.minecraftforge.fml.relauncher.Side;
    import net.minecraftforge.fml.relauncher.SideOnly;

//==================================================================================================

    import javax.annotation.Nullable;
    import javax.annotation.ParametersAreNonnullByDefault;
    import java.util.ArrayList;
    import java.util.List;

//==================================================================================================
    @Mod.EventBusSubscriber @MethodsReturnNonnullByDefault @ParametersAreNonnullByDefault
    @SuppressWarnings( { "WeakerAccess" , "CanBeFinal" } )
//==================================================================================================

    public class Items {

    //==============================================================================================
    // Structure
    //==============================================================================================


        public static class Stem extends ItemBlock {

        //==========================================================================================
        // Setup
        //==========================================================================================

            public Stem( Block block , String ID ) {
            //--------------------------------------------------------------------------------------
                super( block );
            //--------------------------------------------------------------------------------------

                this.setRegistryName( Base.modId , ID );
                this.setUnlocalizedName( ID );

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

        }


    //==============================================================================================


        public static class Compressed extends Stem {

        //==========================================================================================
        // Setup
        //==========================================================================================

            public Compressed( Block block , String ID ) {
            //--------------------------------------------------------------------------------------
                super( block , ID );
            //--------------------------------------------------------------------------------------

                this.setCreativeTab( CreativeTabs.MISC );
                this.setHasSubtypes( true );

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
        // Usage
        //==========================================================================================

            public static ItemStack copyStack( ItemStack other ) { try {
            //--------------------------------------------------------------------------------------
                ItemStack stack = new ItemStack( other.getItem() , 1 , other.getMetadata() );
            //--------------------------------------------------------------------------------------

                if( !other.hasTagCompound() ) return stack;

            //--------------------------------------------------------------------------------------

                stack.setTagCompound(JsonToNBT.getTagFromJson( other.getTagCompound().toString() ));

            //--------------------------------------------------------------------------------------
                return stack;
            //--------------------------------------------------------------------------------------
            } catch ( NBTException ex ) { ex.printStackTrace(); return other; } }

        //==========================================================================================

            @Nullable public static ItemStack getBaseFromRaw( ItemStack raw ) {
            //--------------------------------------------------------------------------------------

                String  mod   = raw.getItem().getRegistryName().getResourceDomain();
                String  entry = raw.getItem().getRegistryName().getResourcePath();
                Integer meta  = raw.getMetadata();

                NBTTagCompound nbt = raw.hasTagCompound() ? raw.getTagCompound()
                                                          : new NBTTagCompound();

            //--------------------------------------------------------------------------------------
                List<ItemStack> in = new ArrayList<>( Configurations.entries );
            //--------------------------------------------------------------------------------------

                in.removeIf( s -> {
                    ResourceLocation loc = s.getItem().getRegistryName();
                    return null == loc || !loc.getResourceDomain().equals( mod );
                } );

                in.removeIf( s -> {
                    ResourceLocation loc = s.getItem().getRegistryName();
                    return null == loc || !loc.getResourcePath().equals( entry );
                } );

                in.removeIf( s -> !meta.equals( s.getMetadata() ) );

                in.removeIf( s -> {
                    NBTTagCompound tag = s.getTagCompound();
                    return !tag.toString().replace( " ", "" ).toLowerCase().contains(
                            nbt.toString().replace( " ", "" ).toLowerCase() );
                } );

            //--------------------------------------------------------------------------------------

                return in.isEmpty() ? null : in.get( 0 );

            //--------------------------------------------------------------------------------------
            }

            public static ItemStack getFromRaw( ItemStack raw , Integer height ) {
            //--------------------------------------------------------------------------------------

                ItemStack base  = getBaseFromRaw( raw );
                Integer   width = base.getSubCompound( "Compression" ).getInteger( "Width" );

            //--------------------------------------------------------------------------------------

                ItemStack stack = new ItemStack( Items.compressed , 1 , 0 );

            //--------------------------------------------------------------------------------------
                NBTTagCompound tag = new NBTTagCompound();
            //--------------------------------------------------------------------------------------

                tag.setInteger( "Width"  , width  );
                tag.setInteger( "Height" , height );

                tag.setString( "Mod"   , base.getItem().getRegistryName().getResourceDomain() );
                tag.setString( "Entry" , base.getItem().getRegistryName().getResourcePath()   );

                tag.setInteger( "Meta" , base.getMetadata() );

            //--------------------------------------------------------------------------------------

                NBTTagCompound nbt = new NBTTagCompound();

            //--------------------------------------------------------------------------------------
                if( raw.hasTagCompound() ) try {
            //--------------------------------------------------------------------------------------

                    nbt = JsonToNBT.getTagFromJson( raw.getTagCompound().toString() );

            //--------------------------------------------------------------------------------------
                } catch ( NBTException ex ) { ex.printStackTrace(); }
            //--------------------------------------------------------------------------------------

                tag.setTag( "NBT" , nbt );

                stack.setTagCompound( tag );

            //--------------------------------------------------------------------------------------

                stack.setStackDisplayName( base.getDisplayName() );
                stack.setStackDisplayName(     width      + " " + stack.getDisplayName() );
                stack.setStackDisplayName( ( 1 + height ) + "x" + stack.getDisplayName() );

            //--------------------------------------------------------------------------------------

                return stack;

            //--------------------------------------------------------------------------------------
            }

            public static ItemStack getRaw( ItemStack compressed ) {
            //--------------------------------------------------------------------------------------

                String         mod   = compressed.getTagCompound().getString     ( "Mod"   );
                String         entry = compressed.getTagCompound().getString     ( "Entry" );
                Integer        meta  = compressed.getTagCompound().getInteger    ( "Meta"  );
                NBTTagCompound nbt   = compressed.getTagCompound().getCompoundTag( "NBT"   );

            //----------------------------------------------------------------------------------

                ResourceLocation loc   = new ResourceLocation( mod , entry );
                ItemStack        stack = new ItemStack( Item.REGISTRY.getObject(loc), 1, meta );

                if( !nbt.hasNoTags() ) stack.setTagCompound( nbt );

                return stack;

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public void getSubItems(CreativeTabs tab , NonNullList<ItemStack> items) {
            //--------------------------------------------------------------------------------------
                //org.lwjgl.input.Mouse.setGrabbed( false );
            //--------------------------------------------------------------------------------------

                Boolean acceptable = tab.equals( CreativeTabs.SEARCH );
                        acceptable = tab.equals( CreativeTabs.MISC ) || acceptable;

                if( !acceptable ) return;

            //--------------------------------------------------------------------------------------
                for( ItemStack entry : Configurations.entries ) { try {
            //--------------------------------------------------------------------------------------

                    String  json   = entry.getSubCompound( "Compression" ).toString();

                    Integer width  = JsonToNBT.getTagFromJson( json ).getInteger( "Width"  );
                    Integer height = JsonToNBT.getTagFromJson( json ).getInteger( "Height" );

                //----------------------------------------------------------------------------------
                    for( int i = 0; i < height; i++ ) {
                //----------------------------------------------------------------------------------

                        ItemStack stack = new ItemStack( Items.compressed , 1 , 0 );

                        stack.setTagCompound( JsonToNBT.getTagFromJson( json ) );
                        stack.getTagCompound().setInteger( "Height" , i );

                        stack.setStackDisplayName( "" + (1 + i) + "x" + width + " " +
                                entry.getDisplayName());

                        items.add( stack );

            //--------------------------------------------------------------------------------------
                } } catch( NBTException ex ) { ex.printStackTrace(); } }
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @SideOnly( Side.CLIENT ) @Override public boolean hasEffect( ItemStack stack ) {
            //--------------------------------------------------------------------------------------

                NBTTagCompound compression = stack.getTagCompound();

            //--------------------------------------------------------------------------------------
                if( null == compression ) return true; if( compression.hasNoTags() ) return true;
            //--------------------------------------------------------------------------------------

                String         mod    = compression.getString     ( "Mod"   );
                String         entry  = compression.getString     ( "Entry" );
                Integer        meta   = compression.getInteger    ( "Meta"  );
                NBTTagCompound nbt    = compression.getCompoundTag( "NBT"   );

            //--------------------------------------------------------------------------------------
                List<ItemStack> in = new ArrayList<>( Configurations.entries );
            //--------------------------------------------------------------------------------------

                in.removeIf( s -> {
                    ResourceLocation loc = s.getItem().getRegistryName();
                    return null == loc || !loc.getResourceDomain().equals( mod );
                } );

                in.removeIf( s -> {
                    ResourceLocation loc = s.getItem().getRegistryName();
                    return null == loc || !loc.getResourcePath().equals( entry );
                } );

                in.removeIf( s -> !meta.equals( s.getMetadata() ) );

                in.removeIf( s -> {
                    NBTTagCompound tag = s.getTagCompound();
                    return null == tag || !tag.toString().replace( " ", "" ).toLowerCase().contains(
                                           nbt.toString().replace( " ", "" ).toLowerCase() );
                } );

            //--------------------------------------------------------------------------------------
                return in.isEmpty() ? true : in.get( 0 ).hasEffect();
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public int getItemBurnTime( ItemStack item ) {  return 0;/*
            //------------------------------------------------------------------------------
                Block AIR = net.minecraft.init.Blocks.AIR;
            //------------------------------------------------------------------------------

                ItemStack base = getRaw( item );

                Integer height = item.getTagCompound().getInteger( "Height" );
                Integer width  = item.getTagCompound().getInteger( "Width" );

                Integer multi = (int) Math.pow( width , height + 1 );

                Block    block    = Block.getBlockFromItem( base.getItem() );
                Material material = block.getDefaultState().getMaterial();

                if( AIR != block && !material.getCanBurn() ) return 0;
                if( AIR == block && !TileEntityFurnace.isItemFuel( base ) ) return 0;

                //------------------------------------------------------------------------------

                Integer defBurnTime = TileEntityFurnace.getItemBurnTime( base );
                Integer stmBurnTime = base.getItem().getItemBurnTime( base );

                if( stmBurnTime >  0 ) return stmBurnTime * item.getCount() * multi;
                else if( stmBurnTime == 0 ) return 0;
                else if( 0 != defBurnTime ) return defBurnTime * item.getCount() * multi;

                //------------------------------------------------------------------------------

                return 300 * item.getCount() * multi;//*/

                //------------------------------------------------------------------------------
            }
        //==========================================================================================
        }


    //==============================================================================================
    // Controls
    //==============================================================================================

        public static Compressed compressed = new Compressed( Blocks.compressed, "compressed" );

    //==============================================================================================
    // Setup
    //==============================================================================================

        @SubscribeEvent public static void Register( Register<Item> event ) {
        //------------------------------------------------------------------------------------------

            event.getRegistry().register( Items.compressed );

        //------------------------------------------------------------------------------------------
        }

    //==============================================================================================

    }

//==================================================================================================
