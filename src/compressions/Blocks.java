//==================================================================================================

    package compressions;

//==================================================================================================

    import mcp.MethodsReturnNonnullByDefault;
    import net.minecraft.block.Block;
    import net.minecraft.block.material.Material;
    import net.minecraft.block.properties.IProperty;
    import net.minecraft.block.state.BlockStateContainer;
    import net.minecraft.block.state.IBlockState;
    import net.minecraft.client.Minecraft;
    import net.minecraft.client.renderer.block.model.BakedQuad;
    import net.minecraft.creativetab.CreativeTabs;
    import net.minecraft.item.ItemStack;
    import net.minecraft.nbt.NBTBase;
    import net.minecraft.nbt.NBTTagCompound;
    import net.minecraft.nbt.NBTTagList;
    import net.minecraft.tileentity.TileEntity;
    import net.minecraft.util.BlockRenderLayer;
    import net.minecraft.util.EnumFacing;
    import net.minecraft.util.NonNullList;
    import net.minecraft.util.math.BlockPos;
    import net.minecraft.world.IBlockAccess;
    import net.minecraft.world.World;
    import net.minecraft.world.storage.MapStorage;
    import net.minecraft.world.storage.WorldSavedData;
    import net.minecraftforge.common.property.ExtendedBlockState;
    import net.minecraftforge.common.property.IExtendedBlockState;
    import net.minecraftforge.common.property.IUnlistedProperty;
    import net.minecraftforge.event.RegistryEvent.Register;
    import net.minecraftforge.event.world.WorldEvent;
    import net.minecraftforge.fml.client.registry.ClientRegistry;
    import net.minecraftforge.fml.common.Mod;
    import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
    import net.minecraftforge.fml.relauncher.Side;
    import net.minecraftforge.fml.relauncher.SideOnly;

//==================================================================================================

    import javax.annotation.Nullable;
    import javax.annotation.ParametersAreNonnullByDefault;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

//==================================================================================================
    @Mod.EventBusSubscriber @MethodsReturnNonnullByDefault @ParametersAreNonnullByDefault
    @SuppressWarnings( { "WeakerAccess" , "CanBeFinal" } )
//==================================================================================================

    public class Blocks {
    //==============================================================================================
    // Structure
    //==============================================================================================
        public static Map<Integer , Map<BlockPos , ItemStack>> placed = new HashMap<>();
    //==============================================================================================


        public static class Data extends WorldSavedData {

        //==========================================================================================

            public Data( String name ) { super( name ); }

        //==========================================================================================

            public static Data Load( World world ) {
            //--------------------------------------------------------------------------------------

                MapStorage storage = world.getPerWorldStorage();

                Data data = (Data) storage.getOrLoadData( Data.class, "compressions" );

                if( null == data ) { data = new Data( "compressions" );
                                     storage.setData( "compressions" , data ); }

                return data;

            //--------------------------------------------------------------------------------------
            }

        //======================================================================================

            @Override public void readFromNBT( NBTTagCompound compound ) {
            //--------------------------------------------------------------------------------------

                placed.clear();

            //--------------------------------------------------------------------------------------
                for( NBTBase base : compound.getTagList( "Placed" , compound.getId() ) ) {
            //--------------------------------------------------------------------------------------

                    NBTTagCompound tag = (NBTTagCompound) base;

                //----------------------------------------------------------------------------------

                    Integer dimID = tag.getInteger( "Dimension" );

                    if( !placed.containsKey( dimID ) ) placed.put( dimID , new HashMap<>() );

                //----------------------------------------------------------------------------------

                    Integer posX  = tag.getInteger( "PosX" );
                    Integer posY  = tag.getInteger( "PosY" );
                    Integer posZ  = tag.getInteger( "PosZ" );

                    BlockPos position = new BlockPos( posX , posY , posZ );

                //----------------------------------------------------------------------------------

                    Integer width  = tag.getInteger( "Width" );
                    Integer height = tag.getInteger( "Height" );

                    String  mod   = tag.getString( "Mod" );
                    String  entry = tag.getString( "Entry" );
                    Integer meta  = tag.getInteger( "Meta" );

                    NBTTagCompound nbt = tag.getCompoundTag( "NBT" );

                    NBTTagCompound stacktag = new NBTTagCompound();

                    stacktag.setInteger( "Width"  , width  );
                    stacktag.setInteger( "Height" , height );
                    stacktag.setString ( "Mod"    , mod    );
                    stacktag.setString ( "Entry"  , entry  );
                    stacktag.setInteger( "Meta"   , meta   );
                    stacktag.setTag    ( "NBT"    , nbt    );

                    ItemStack stack = new ItemStack( Items.compressed , 1 , 0 );
                    stack.setTagCompound( stacktag );

                //----------------------------------------------------------------------------------

                    placed.get( dimID ).put( position , stack );

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------
            }

            @Override public NBTTagCompound writeToNBT( NBTTagCompound compound ) {
            //--------------------------------------------------------------------------------------

                NBTTagList list = new NBTTagList();

            //--------------------------------------------------------------------------------------
                for(Integer dim : placed.keySet()) { for(BlockPos pos : placed.get(dim).keySet()) {
            //--------------------------------------------------------------------------------------

                    NBTTagCompound nbt = placed.get( dim ).get( pos ).getTagCompound();

                //----------------------------------------------------------------------------------

                    NBTTagCompound tag = new NBTTagCompound();

                    tag.setInteger( "Dimension" , dim );
                    tag.setInteger( "PosX" , pos.getX() );
                    tag.setInteger( "PosY" , pos.getY() );
                    tag.setInteger( "PosZ" , pos.getZ() );

                    tag.setInteger( "Width"  , nbt.getInteger( "Width"  ) );
                    tag.setInteger( "Height" , nbt.getInteger( "Height" ) );
                    tag.setString ( "Mod"    , nbt.getString ( "Mod"    ) );
                    tag.setString ( "Entry"  , nbt.getString ( "Entry"  ) );
                    tag.setInteger( "Meta"   , nbt.getInteger( "Meta"   ) );
                    tag.setTag    ( "NBT"    , nbt.getTag    ( "NBT"    ) );

                    list.appendTag( tag );

            //--------------------------------------------------------------------------------------
                } }
            //--------------------------------------------------------------------------------------

                compound.setTag( "Placed" , list );

            //--------------------------------------------------------------------------------------
                return compound;
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
        }


    //==============================================================================================


        public static class Stem extends Block {

        //==========================================================================================

            public static ItemStack temp = null;

        //==========================================================================================
        // Structure
        //==========================================================================================


            public static class UnlistedInteger implements IUnlistedProperty<Integer> {

            //======================================================================================

                String name;

            //======================================================================================

                UnlistedInteger( String name ) {
                //----------------------------------------------------------------------------------
                    this.name = name;
                //----------------------------------------------------------------------------------
                }

            //======================================================================================

                @Override public String getName() {
                //----------------------------------------------------------------------------------
                    return this.name;
                //----------------------------------------------------------------------------------
                }

                @Override public boolean isValid( Integer value ) {
                //----------------------------------------------------------------------------------
                    return true;
                //----------------------------------------------------------------------------------
                }

                @Override public Class<Integer> getType() {
                //----------------------------------------------------------------------------------
                    return Integer.class;
                //----------------------------------------------------------------------------------
                }

                @Override public String valueToString( Integer value ) {
                //----------------------------------------------------------------------------------
                    return "" + value;
                //----------------------------------------------------------------------------------
                }

            //======================================================================================

            }

            public static class TEData extends TileEntity {

            //======================================================================================

                ItemStack stack;

            //======================================================================================

                TEData(ItemStack stack ) { this.stack = stack; }

            //======================================================================================

            }


        //==========================================================================================
        // Setup
        //==========================================================================================

            public Stem( Material material , String ID ) {
            //--------------------------------------------------------------------------------------
                super( material );
            //--------------------------------------------------------------------------------------

                this.setRegistryName( Base.modId , ID );
                this.setUnlocalizedName( ID );

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
        // In world description
        //==========================================================================================

            @Override protected BlockStateContainer createBlockState() {
            //--------------------------------------------------------------------------------------

                IUnlistedProperty[] properties = new IUnlistedProperty[] {
                //----------------------------------------------------------------------------------

                        new UnlistedInteger( "PosX" ) ,
                        new UnlistedInteger( "PosY" ) ,
                        new UnlistedInteger( "PosZ" ) ,

                //----------------------------------------------------------------------------------
                };

                return new ExtendedBlockState( this , new IProperty[0] , properties );

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public IBlockState getExtendedState(
            //--------------------------------------------------------------------------------------
                    IBlockState  state    ,
                    IBlockAccess world    ,
                    BlockPos     position
            //--------------------------------------------------------------------------------------
            ) {
            //--------------------------------------------------------------------------------------

                IExtendedBlockState extended = (IExtendedBlockState) state;

            //--------------------------------------------------------------------------------------
                for( IUnlistedProperty property : extended.getUnlistedNames() ) {
            //--------------------------------------------------------------------------------------

                    if( !(property instanceof UnlistedInteger) ) continue;

                    UnlistedInteger integer = (UnlistedInteger) property;

                //----------------------------------------------------------------------------------

                    if( property.getName().equals( "PosX" ) )
                        extended = extended.withProperty( integer , position.getX() );

                    if( property.getName().equals( "PosY" ) )
                        extended = extended.withProperty( integer , position.getY() );

                    if( property.getName().equals( "PosZ" ) )
                        extended = extended.withProperty( integer , position.getZ() );

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------

                return extended;

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
        // In world tracking
        //==========================================================================================

            @Nullable public static ItemStack getPlaced( @Nullable IBlockState state ) {
            //--------------------------------------------------------------------------------------
                if( null == state || !( state instanceof IExtendedBlockState ) ) return null;
            //--------------------------------------------------------------------------------------

                IExtendedBlockState extState = (IExtendedBlockState) state;

                Integer posX = null;
                Integer posY = null;
                Integer posZ = null;

                for( IUnlistedProperty prop : extState.getUnlistedNames() ) {
                    if( prop.getName().equals("PosX") ) posX = (Integer) extState.getValue( prop );
                    if( prop.getName().equals("PosY") ) posY = (Integer) extState.getValue( prop );
                    if( prop.getName().equals("PosZ") ) posZ = (Integer) extState.getValue( prop );
                }

            //--------------------------------------------------------------------------------------
                if( null == posX || null == posY || null == posZ ) return null;
            //--------------------------------------------------------------------------------------

                BlockPos pos = new BlockPos( posX , posY , posZ );

            //--------------------------------------------------------------------------------------
                if( null == Minecraft.getMinecraft().world ) return null;
            //--------------------------------------------------------------------------------------

                Integer dimID = Minecraft.getMinecraft().world.provider.getDimension();

            //--------------------------------------------------------------------------------------
                if( !Blocks.placed.containsKey( dimID ) ) return null;
            //--------------------------------------------------------------------------------------

                ItemStack stack = Blocks.placed.get( dimID ).getOrDefault( pos , null );

            //--------------------------------------------------------------------------------------
                return stack;
            //--------------------------------------------------------------------------------------
            }

            @Nullable public static ItemStack getPlaced( @Nullable BlockPos pos ) {
            //--------------------------------------------------------------------------------------
                if( null == Minecraft.getMinecraft().world ) return null;
            //--------------------------------------------------------------------------------------

                Integer dimID = Minecraft.getMinecraft().world.provider.getDimension();

            //--------------------------------------------------------------------------------------
                if( !Blocks.placed.containsKey( dimID ) ) return null;
            //--------------------------------------------------------------------------------------

                ItemStack stack = Blocks.placed.get( dimID ).getOrDefault( pos , null );

            //--------------------------------------------------------------------------------------
                return stack;
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public void breakBlock( World world , BlockPos pos , IBlockState state ) {
            //--------------------------------------------------------------------------------------

                Integer id = world.provider.getDimension();

                if( !Blocks.placed.containsKey( id ) ) Blocks.placed.put(id , new HashMap<>());

                Blocks.placed.get( id ).remove( pos );

            //--------------------------------------------------------------------------------------
                if( !world.isRemote ) Data.Load( world ).markDirty();
            //--------------------------------------------------------------------------------------
                if( !world.isRemote ) super.breakBlock( world , pos , state );
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
        // Properties
        //==========================================================================================

            @Override @SideOnly( Side.CLIENT ) public BlockRenderLayer getBlockLayer() {
            //--------------------------------------------------------------------------------------
                return BlockRenderLayer.CUTOUT;
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public boolean hasTileEntity( IBlockState state ) {
            //--------------------------------------------------------------------------------------
             /*   org.lwjgl.input.Mouse.setGrabbed(false);
            //--------------------------------------------------------------------------------------

                boolean looped = false;

                for( StackTraceElement element : Thread.currentThread().getStackTrace() ) {

                    if( element.getClassName().startsWith( "compressions" ) )
                        if( element.getMethodName().equals("hasTileEntity") )
                            continue;

                    if( element.getClassName().startsWith( "compressions" ) ) looped = true;
                    if( element.getClassName().startsWith( "compressions" ) ) break;
                }

                ItemStack stack = getPlaced( state );

                if( looped && null != temp ) stack = temp;

            //--------------------------------------------------------------------------------------
                if( null == stack ) return false;
            //--------------------------------------------------------------------------------------

                List<BakedQuad> quads = Models.Compressed.overrides
                                              .handleItemState( null , stack , null , null )
                                              .getQuads( state , EnumFacing.NORTH , 0 );

                if( 2 == quads.size() )
                    return true;

            //--------------------------------------------------------------------------------------
                return false;//*/ return true;
            //--------------------------------------------------------------------------------------
            }

            @Override public TileEntity createTileEntity( World world , IBlockState state ) {
            //--------------------------------------------------------------------------------------

                boolean looped = false;

                for( StackTraceElement element : Thread.currentThread().getStackTrace() ) {

                    if( element.getClassName().startsWith( "compressions" ) )
                        if( element.getMethodName().equals("hasTileEntity") )
                            continue;

                    if( element.getClassName().startsWith( "compressions" ) ) looped = true;
                    if( element.getClassName().startsWith( "compressions" ) ) break;
                }

                ItemStack stack = getPlaced( state );

                if( looped && null != temp ) stack = temp;

                if( null == stack ) return null;

                List<BakedQuad> quads = Models.Compressed.overrides
                                              .handleItemState( null , stack , null , null )
                                              .getQuads( state , EnumFacing.NORTH , 0 );

                if( 2 == quads.size() ) return new TEData( getPlaced( state ) );

                return null;
            //--------------------------------------------------------------------------------------
            }


        //==========================================================================================
        }


    //==============================================================================================


        public static class Compressed extends Stem {

        //==========================================================================================
        // Setup
        //==========================================================================================

            public Compressed( Material material , String ID ) {
            //--------------------------------------------------------------------------------------
                super( material , ID );
            //--------------------------------------------------------------------------------------

                this.setCreativeTab( CreativeTabs.MISC );

                //this.setDefaultState

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
        // Usage
        //==========================================================================================

            @Override public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
            //--------------------------------------------------------------------------------------

                Items.compressed.getSubItems( tab , items );

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
        // Properties
        //==========================================================================================

        //==========================================================================================

        }


    //==============================================================================================
    // Controls
    //==============================================================================================

        public static Compressed compressed = new Compressed( Material.WOOD , "compressed" );

    //==============================================================================================
    // Setup
    //==============================================================================================

        @SubscribeEvent public static void Register( Register<Block> event ) {
        //------------------------------------------------------------------------------------------

            event.getRegistry().register( Blocks.compressed );

            ClientRegistry.registerTileEntity( Stem.TEData.class ,
                    Blocks.compressed.getRegistryName().toString() , new Renderers.CmprTE() );

        //------------------------------------------------------------------------------------------
        }

    //==============================================================================================

        @SubscribeEvent public static void Load( WorldEvent.Load event ) {
        //------------------------------------------------------------------------------------------

            Data.Load( event.getWorld() ).markDirty();

        //------------------------------------------------------------------------------------------
        }

    //==============================================================================================

    }

//==================================================================================================
