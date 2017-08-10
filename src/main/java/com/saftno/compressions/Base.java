//==================================================================================

    package com.saftno.compressions;

//==================================================================================

    import net.minecraft.block.Block;
    import net.minecraft.item.Item;
    import net.minecraft.item.ItemStack;
    import net.minecraft.nbt.NBTTagCompound;
    import net.minecraft.util.ResourceLocation;
    import net.minecraftforge.fml.common.Mod;

//==================================================================================

    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.util.ArrayList;
    import java.util.HashSet;
    import java.util.List;
    import java.util.Set;
    import java.util.function.Function;

//==================================================================================
    @Mod.EventBusSubscriber
//----------------------------------------------------------------------------------
    @Mod( modid   = Base.modId   , dependencies = "after:*" ,
          name    = Base.name    , acceptedMinecraftVersions = "[1.12]"  ,
          version = Base.version )
//----------------------------------------------------------------------------------
    @SuppressWarnings( { "WeakerAccess" , "CanBeFinal" } )
//==================================================================================

    public class Base {

    //==============================================================================
    // Entry
    //==============================================================================

        public static final String modId   = "compressions";
        public static final String name    = "Compressions";
        public static final String version = "1.1.5";

    //==============================================================================
        @Mod.Instance( modId )
    //==============================================================================

        public static Base instance;

    //==============================================================================
    // Structure
    //==============================================================================

        public static Path root = Paths.get( System.getProperty( "user.dir" ) );

    //==============================================================================

        public static class Entries<T> implements Iterable<T> {

        //==========================================================================
        // Structure
        //==========================================================================

            List<T>     values = new ArrayList<>();
            Set<String> keys   = new HashSet<>();

        //==========================================================================

            Function<T , String> getID;

        //==========================================================================

            public class Iterator implements java.util.Iterator<T> {
            //======================================================================

                public Entries entries = null;
                public int pos = 0;

            //======================================================================

                public Iterator( Entries entries ) { this.entries = entries; }

                public boolean hasNext() { return pos < entries.values.size(); }

                public T next() { return (T) entries.values.get( pos++ ); }

                public void remove() { throw new UnsupportedOperationException(); }

            //======================================================================

            }

        //==========================================================================
        // Setup
        //==========================================================================

            public Iterator iterator() { return new Iterator( this ); }

        //==========================================================================

            Entries( Function<T , String> getID ) {
                //----------------------------------------------------------------------

                this.getID = getID;

                //----------------------------------------------------------------------
            }

        //==========================================================================
        // Usage
        //==========================================================================

            void Add( T entry ) {
            //----------------------------------------------------------------------
                if( keys.contains( this.getID.apply( entry ) ) ) return;
            //----------------------------------------------------------------------

                keys.add( this.getID.apply( entry ) );
                values.add( entry );

            //----------------------------------------------------------------------
            }

        //==========================================================================

            T Get( Integer i ) {
                //----------------------------------------------------------------------
                if( i >= values.size() ) return null;
                //----------------------------------------------------------------------

                return values.get( i );

                //----------------------------------------------------------------------
            }

        //==========================================================================

            Integer Size()    { return values.size();    }
            Boolean isEmpty() { return values.isEmpty(); }

        //==========================================================================

        }

    //==============================================================================
    // Usage
    //==============================================================================

        public static String UID( ItemStack item ) {
        //--------------------------------------------------------------------------
            String error1 = "'ItemStack' has invalid 'ResourceLocation'";
        //--------------------------------------------------------------------------

            ResourceLocation loc = item.getItem().getRegistryName();

            if( null == loc ) throw new NullPointerException( error1 );

        //--------------------------------------------------------------------------

            String name = loc.getResourceDomain() + '_' + loc.getResourcePath();

        //--------------------------------------------------------------------------

            if( !item.getHasSubtypes() ) return name;

        //--------------------------------------------------------------------------

            name = name + '_' + item.getMetadata();

        //--------------------------------------------------------------------------
            NBTTagCompound tag = item.getTagCompound();
        //--------------------------------------------------------------------------

            if( null == item.getTagCompound() ) return name;

        //--------------------------------------------------------------------------

            String extra = item.getTagCompound().toString();

            extra = extra.replace( "\"", ""  ).replace( " " , ""  );
            extra = extra.replace( "{" , ""  ).replace( "}" , ""  );
            extra = extra.replace( ":" , "_" ).replace( "," , "_" );

            return name + '_' + extra;

        //--------------------------------------------------------------------------
        }

        public static String UID( Item item ) {
        //--------------------------------------------------------------------------
            return item.getRegistryName().toString();
        //--------------------------------------------------------------------------
        }

        public static String UID( Block block ) {
        //--------------------------------------------------------------------------
            return block.getRegistryName().toString();
        //--------------------------------------------------------------------------
        }

    //==============================================================================

    }

//==================================================================================
