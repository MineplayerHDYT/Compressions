//==================================================================================

    package com.saftno.compressions;

//==================================================================================

    import net.minecraft.block.Block;
    import net.minecraft.item.Item;
    import net.minecraft.item.ItemStack;
    import net.minecraft.nbt.NBTTagCompound;
    import net.minecraft.util.ResourceLocation;
    import net.minecraftforge.fml.common.Mod;
    import net.minecraftforge.fml.common.SidedProxy;
    import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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

        public static Boolean dev;
        public static Path    root;
        public static Path    jar;

    //==============================================================================

        static /* Set base locations */ { init: {
        //--------------------------------------------------------------------------

            String thisLocation = Base.class.getProtectionDomain()
                                            .getCodeSource()
                                            .getLocation()
                                            .getFile();

            Logging.info( "Base - " + thisLocation );

        //--------------------------------------------------------------------------

            dev = !thisLocation.startsWith( "file:" );

        //--------------------------------------------------------------------------

            if( dev ) root = Paths.get( System.getProperty( "user.dir" ) );
            if( dev ) break init;

        //--------------------------------------------------------------------------
            Boolean Windows = System.getProperty( "os.name" ).contains( "indow" );
        //--------------------------------------------------------------------------

            thisLocation = thisLocation.split( "file:" )[1].split( "!" )[0];

            jar  = Paths.get( Windows ? thisLocation.substring(1) : thisLocation );
            root = jar.getParent().getParent();

        //--------------------------------------------------------------------------
        } }

    //==============================================================================
        @SidedProxy( serverSide = "com.saftno.compressions.Proxies$Common" ,
                     clientSide = "com.saftno.compressions.Proxies$Client" )
    //==============================================================================

        public static Proxies.Common proxy;

    //==============================================================================

        public static final String modId   = "compressions";
        public static final String name    = "Compressions";
        public static final String version = "1.0.0";

    //==============================================================================
        @Mod.Instance( modId )
    //==============================================================================

        public static Base instance;

    //==============================================================================

        public static boolean once = false;

    //==============================================================================
        @Mod.EventHandler
    //==============================================================================

        public void preInit(FMLPreInitializationEvent event) {
        //--------------------------------------------------------------------------

            System.out.println( name + " is loading" );

        //--------------------------------------------------------------------------
        }

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

        public static class Entries<T> implements Iterable<T> {
        //==========================================================================
        // Setup
        //==========================================================================

            List<T>     values = new ArrayList<>();
            Set<String> keys   = new HashSet<>();

        //==========================================================================

            Function<T , String> getID;

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
        // Iteration
        //==========================================================================

            public Iterator iterator() { return new Iterator( this ); }

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

        }

    //==============================================================================

    }

//==================================================================================
