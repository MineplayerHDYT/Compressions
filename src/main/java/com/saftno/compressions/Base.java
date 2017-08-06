//==================================================================================

    package com.saftno.compressions;

//==================================================================================

    import net.minecraft.block.Block;
    import net.minecraft.client.Minecraft;
    import net.minecraft.client.renderer.block.model.IBakedModel;
    import net.minecraft.client.renderer.block.model.ModelResourceLocation;
    import net.minecraft.item.Item;
    import net.minecraft.item.ItemStack;
    import net.minecraft.item.crafting.IRecipe;
    import net.minecraft.nbt.NBTTagCompound;
    import net.minecraft.util.ResourceLocation;
    import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
    import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
    import net.minecraftforge.client.event.ModelBakeEvent;
    import net.minecraftforge.client.event.ModelRegistryEvent;
    import net.minecraftforge.event.RegistryEvent.Register;
    import net.minecraftforge.fml.common.Mod;
    import net.minecraftforge.fml.common.SidedProxy;
    import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
    import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

//==================================================================================

    import java.nio.IntBuffer;
    import java.util.*;
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

        public static String  root = System.getProperty("user.dir");
        public static boolean once = false;

        @Mod.EventHandler
        public void preInit(FMLPreInitializationEvent event) {

            System.out.println(name + " is loading!");

        }

        //==========================================================================
        // Events
        //==========================================================================

            public static IntBuffer forgeEndScreen;

        //==========================================================================
            @SubscribeEvent
        //==========================================================================

            public static void forgeEnd( InitGuiEvent event ) {
            //----------------------------------------------------------------------

                if( null == forgeEndScreen ) forgeEndScreen = Textures.GrabScreen();

            //----------------------------------------------------------------------
            }

        //==========================================================================
            @SubscribeEvent
        //==========================================================================

            public static void regBlocks( Register<Block> event ) {
            //----------------------------------------------------------------------

                int h = 0;
                //Blocks.Registration.Blocks( event );

            //----------------------------------------------------------------------
            }

        //==========================================================================
            @SubscribeEvent
        //==========================================================================

            public static void regItems( Register<Item> event ) {
            //----------------------------------------------------------------------

                //Blocks.Registration.Items( event );

            //----------------------------------------------------------------------
            }

        //==========================================================================
            @SubscribeEvent
        //==========================================================================

            public static void regRecipes( Register<IRecipe> event ) {
            //----------------------------------------------------------------------


                forgeEndScreen = Textures.GrabScreen();
                //Blocks.Registration.Recipes( event );
                forgeEndScreen = null;


            //----------------------------------------------------------------------

            //----------------------------------------------------------------------
            }

        //==========================================================================
            @SubscribeEvent
        //==========================================================================

            public static void regModels( ModelRegistryEvent event ) {
            //----------------------------------------------------------------------

                //Blocks.Registration.Models( event );

            //----------------------------------------------------------------------
            }

            public static ModelBakeEvent bk;

            @SubscribeEvent public static void regtest( ModelBakeEvent event ) {
            //----------------------------------------------------------------------

                bk = event;


            //----------------------------------------------------------------------
            }

        //==========================================================================
            @SubscribeEvent
        //==========================================================================

            public static void regResourcePacks( DrawScreenEvent event ) {
            //----------------------------------------------------------------------
                if( once ) return;
            //----------------------------------------------------------------------

                Languages.Generation.LANG();
                //Recipes.Generation.JSON();
                Models.Generation.Blockstates();
                Textures.Generation.Blocks();

                for( Item item : Items.items ) {

                    ResourceLocation rLoc = item.getRegistryName();
                    ModelResourceLocation mrLoc = new ModelResourceLocation(rLoc,
                            "inventory");

                    //----------------------------------------------------------------------

                    ItemStack stack = new ItemStack( item , 1 , 0 );
                    IBakedModel model = Minecraft.getMinecraft().getRenderItem()
                            .getItemModelMesher().getItemModel( stack );

                    String name = model.toString();
                    if( name.contains( "FancyMissingModel" ) ) continue;

                    Minecraft.getMinecraft().getRenderItem()
                            .getItemModelMesher().register( item , 0 , mrLoc );

                    //ModelLoader.setCustomModelResourceLocation( item , 0 ,
                    // mrLoc );
                    //ModelLoader.setCustomModelResourceLocation( item , 0 ,
                    //        mrLoc2 );
                    //IBakedModel mod = bk.getModelManager().getModel(mrLoc);

                    //bk.getModelRegistry().putObject(mrLoc , mod);

                }
                //bk.getModelLoader().setupModelRegistry();

            //----------------------------------------------------------------------

                Resources.Registration.Packs();

            //----------------------------------------------------------------------

                //Textures.Generation.saveAllToFile();

            //----------------------------------------------------------------------
                once = true; Logging.info( name + " finished loading" );
            //----------------------------------------------------------------------
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
