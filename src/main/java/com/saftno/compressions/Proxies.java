//==================================================================================

    package com.saftno.compressions;

//==================================================================================

    import net.minecraft.client.renderer.block.model.ModelResourceLocation;
    import net.minecraft.item.Item;
    import net.minecraft.util.ResourceLocation;
    import net.minecraftforge.client.model.ModelLoader;

//==================================================================================

    public class Proxies {

    //==============================================================================

        public static class Common {

        //==========================================================================

            public <T extends Blocks.Stem> void registerBlockRenderer(T block ) {
            //----------------------------------------------------------------------

            //----------------------------------------------------------------------
            }

        //==========================================================================

        }

        public static class Client extends Common {

        //==========================================================================

            public <T extends Blocks.Stem> void registerBlockRenderer(T block) {
            //----------------------------------------------------------------------
                String var = "inventory";
            //----------------------------------------------------------------------

                ResourceLocation      rLoc  = block.getRegistryName();

            //----------------------------------------------------------------------
                if( null == rLoc) return;
            //----------------------------------------------------------------------

                ModelResourceLocation mrLoc = new ModelResourceLocation(rLoc, var);

            //----------------------------------------------------------------------

                Item item = block.getAsItem();
                ModelLoader.setCustomModelResourceLocation( item , 0 , mrLoc );

            //----------------------------------------------------------------------
            }

        //==========================================================================

        }

    //==============================================================================

    }

//==================================================================================

