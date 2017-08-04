//==================================================================================

    package com.saftno.compressions;

//==================================================================================

    import net.minecraft.block.Block;
    import net.minecraft.client.Minecraft;
    import net.minecraft.client.renderer.BlockModelShapes;
    import net.minecraft.client.renderer.block.model.ModelManager;
    import net.minecraft.client.renderer.block.model.ModelResourceLocation;
    import net.minecraft.item.Item;
    import net.minecraft.util.ResourceLocation;
    import net.minecraftforge.client.model.ModelLoader;
    import net.minecraftforge.common.model.Models;
    import net.minecraftforge.fml.common.registry.ForgeRegistries;

    import java.lang.reflect.Field;

//==================================================================================

    public class Proxies {

    //==============================================================================

        public static class Common {

        //==========================================================================

            public <T extends Block> void registerBlockRenderer(T block ) {
            //----------------------------------------------------------------------

            //----------------------------------------------------------------------
            }

        //==========================================================================

        }

        public static class Client extends Common {

        //==========================================================================

            public <T extends Block> void registerBlockRenderer(T block)
            {//try{
            //----------------------------------------------------------------------
                String var = "normal";
            //----------------------------------------------------------------------

                ResourceLocation rLoc = block.getRegistryName();

            //----------------------------------------------------------------------
                if( null == rLoc) return;
            //----------------------------------------------------------------------

                ModelResourceLocation mrLoc = new ModelResourceLocation(rLoc, var);

            //----------------------------------------------------------------------

                //Item item = block.getAsItem();
                //ModelLoader.setCustomModelResourceLocation( item , 0 , mrLoc );

                //Field f = ModelLoader.class.getDeclaredField("modelProvider");
                //f.setAccessible( true );
                //BlockModelShapes modelProvider = (BlockModelShapes) f.get
                //        (ModelLoader);

            //----------------------------------------------------------------------
            } //catch( NoSuchFieldException ex ) { ex.printStackTrace(); } }

        //==========================================================================

        }

    //==============================================================================

    }

//==================================================================================

