//==============================================================================================

    package com.saftno.compressions;

//==============================================================================================

    import com.google.common.io.ByteStreams;
    import com.saftno.compressions.ItemBlocks.Compressed;
    import com.saftno.compressions.ItemBlocks.Compressed.ItemX;
    import com.saftno.compressions.ResourcePacks.Type;

//==============================================================================================

    import io.netty.buffer.ByteBufInputStream;
    import net.minecraft.block.Block;
    import net.minecraft.block.BlockLever;
    import net.minecraft.block.properties.IProperty;
    import net.minecraft.block.properties.PropertyDirection;
    import net.minecraft.block.state.IBlockState;
    import net.minecraft.client.Minecraft;
    import net.minecraft.client.renderer.RenderItem;
    import net.minecraft.client.renderer.block.model.BakedQuad;
    import net.minecraft.client.renderer.block.model.IBakedModel;
    import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
    import net.minecraft.client.renderer.texture.TextureManager;
    import net.minecraft.client.renderer.texture.TextureMap;
    import net.minecraft.client.shader.Framebuffer;
    import net.minecraft.init.Blocks;
    import net.minecraft.init.Items;
    import net.minecraft.item.Item;
    import net.minecraft.item.ItemBed;
    import net.minecraft.item.ItemBlock;
    import net.minecraft.item.ItemStack;
    import net.minecraft.util.EnumFacing;
    import net.minecraft.util.ResourceLocation;
    import net.minecraft.util.math.AxisAlignedBB;
    import net.minecraftforge.client.ForgeHooksClient;
    import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
    import net.minecraftforge.fml.common.Mod;
    import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
    import org.apache.commons.io.FileUtils;
    import org.lwjgl.BufferUtils;
    import org.lwjgl.opengl.Display;
    import org.lwjgl.opengl.GL11;

//==============================================================================================

    import javax.imageio.ImageIO;
    import java.awt.Color;
    import java.awt.image.BufferedImage;
    import java.io.IOException;
    import java.io.InputStream;
    import java.io.OutputStream;
    import java.nio.FloatBuffer;
    import java.nio.IntBuffer;
    import java.nio.file.FileSystem;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.util.stream.IntStream;

//==============================================================================================
    @SuppressWarnings( { "WeakerAccess" , "unused" } )  @Mod.EventBusSubscriber
//==============================================================================================

    public class Textures {

    //==========================================================================================
    // Structure
    //==========================================================================================

        public static int[][] frame;
        public static int[][] wooden;

    //==========================================================================================

        public static final Integer w3D = 256;
        public static final Integer h3D = 256;

    //==========================================================================================

        public static IntBuffer ForgeEndScreen;

    //==========================================================================================
    // Setup
    //==========================================================================================

        static /* Setting up frame data */ {
        //--------------------------------------------------------------------------------------

            int[] bytes = new int[] {   0x24 , 0x19 , 0x09 , 0xff , 0x2a , 0x1f , 0x11 , 0xff ,
                                        0x33 , 0x25 , 0x10 , 0xff , 0x3e , 0x29 , 0x16 , 0xff ,
                                        0x19 , 0x10 , 0x07 , 0xff , 0x23 , 0x19 , 0x0f , 0xff ,
                                        0x3e , 0x2e , 0x15 , 0xff , 0x1c , 0x13 , 0x09 , 0xff ,
                                        0x1b , 0x11 , 0x07 , 0xff , 0x36 , 0x23 , 0x13 , 0xff ,
                                        0x33 , 0x26 , 0x12 , 0xff , 0x2e , 0x1e , 0x0d , 0xff ,
                                        0x0e , 0x0a , 0x04 , 0xff , 0x18 , 0x11 , 0x09 , 0xff ,
                                        0x11 , 0x0b , 0x05 , 0xff , 0x16 , 0x0e , 0x07 , 0xff ,
                                        0x27 , 0x19 , 0x0c , 0xff , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x2a , 0x1a , 0x0e , 0xff ,
                                        0x1e , 0x14 , 0x08 , 0xff , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x1c , 0x12 , 0x09 , 0xff ,
                                        0x2e , 0x23 , 0x14 , 0xff , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x29 , 0x1c , 0x0b , 0xff ,
                                        0x1b , 0x14 , 0x08 , 0xff , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x19 , 0x11 , 0x09 , 0xff ,
                                        0x1b , 0x11 , 0x07 , 0xff , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x0f , 0x09 , 0x04 , 0xff ,
                                        0x14 , 0x0e , 0x07 , 0xff , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x39 , 0x26 , 0x0f , 0xff ,
                                        0x14 , 0x0c , 0x06 , 0xff , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x08 , 0x06 , 0x02 , 0xff ,
                                        0x19 , 0x11 , 0x07 , 0xff , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x1c , 0x14 , 0x09 , 0xff ,
                                        0x12 , 0x0b , 0x06 , 0xff , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x26 , 0x19 , 0x0e , 0xff ,
                                        0x12 , 0x0c , 0x07 , 0xff , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x2a , 0x17 , 0x0c , 0xff ,
                                        0x34 , 0x23 , 0x0f , 0xff , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x29 , 0x1e , 0x0f , 0xff ,
                                        0x31 , 0x22 , 0x11 , 0xff , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x11 , 0x0c , 0x04 , 0xff ,
                                        0x31 , 0x21 , 0x10 , 0xff , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x45 , 0x2e , 0x19 , 0xff ,
                                        0x25 , 0x19 , 0x0e , 0xff , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 ,
                                        0x00 , 0x00 , 0x00 , 0x00 , 0x25 , 0x18 , 0x0b , 0xff ,
                                        0x27 , 0x19 , 0x0c , 0xff , 0x32 , 0x1f , 0x0f , 0xff ,
                                        0x24 , 0x1b , 0x0a , 0xff , 0x3e , 0x26 , 0x16 , 0xff ,
                                        0x1d , 0x12 , 0x09 , 0xff , 0x38 , 0x23 , 0x11 , 0xff ,
                                        0x2c , 0x1e , 0x0f , 0xff , 0x11 , 0x0c , 0x05 , 0xff ,
                                        0x33 , 0x23 , 0x0f , 0xff , 0x26 , 0x1a , 0x0c , 0xff ,
                                        0x09 , 0x06 , 0x04 , 0xff , 0x2f , 0x21 , 0x10 , 0xff ,
                                        0x29 , 0x1c , 0x0d , 0xff , 0x23 , 0x19 , 0x0d , 0xff ,
                                        0x25 , 0x19 , 0x0e , 0xff , 0x1b , 0x12 , 0x0a , 0xff };

        //--------------------------------------------------------------------------------------

            int h = 16;
            int w = 16;

            frame = new int[h][w];

        //--------------------------------------------------------------------------------------
            for( int y = 0; y < h; y++ ) { for( int x = 0; x < w; x++ ) {
        //--------------------------------------------------------------------------------------

                int R = bytes[y * 16 * 4 + x * 4 + 0] & 255;
                int G = bytes[y * 16 * 4 + x * 4 + 1] & 255;
                int B = bytes[y * 16 * 4 + x * 4 + 2] & 255;
                int A = bytes[y * 16 * 4 + x * 4 + 3] & 255;

                frame[y][x] = ( R << 24 ) | ( G << 16 ) | ( B << 8 ) | ( A );

        //--------------------------------------------------------------------------------------
            } }
        //--------------------------------------------------------------------------------------
        }

        static /* Setting up wooden side data */  {
        //--------------------------------------------------------------------------------------

            int[] bytes = new int[] {   0x9a , 0x9a , 0x9a , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x92 , 0x92 , 0x92 , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x92 , 0x92 , 0x92 , 0xff , 0x5f , 0x5f , 0x5f , 0xff ,
                                        0x92 , 0x92 , 0x92 , 0xff , 0x92 , 0x92 , 0x92 , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x92 , 0x92 , 0x92 , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x92 , 0x92 , 0x92 , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x92 , 0x92 , 0x92 , 0xff , 0x92 , 0x92 , 0x92 , 0xff ,
                                        0x5f , 0x5f , 0x5f , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x92 , 0x92 , 0x92 , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x55 , 0x55 , 0x55 , 0xff , 0x55 , 0x55 , 0x55 , 0xff ,
                                        0x55 , 0x55 , 0x55 , 0xff , 0x5f , 0x5f , 0x5f , 0xff ,
                                        0x5f , 0x5f , 0x5f , 0xff , 0x5f , 0x5f , 0x5f , 0xff ,
                                        0x5f , 0x5f , 0x5f , 0xff , 0x5f , 0x5f , 0x5f , 0xff ,
                                        0x5f , 0x5f , 0x5f , 0xff , 0x5f , 0x5f , 0x5f , 0xff ,
                                        0x55 , 0x55 , 0x55 , 0xff , 0x55 , 0x55 , 0x55 , 0xff ,
                                        0x55 , 0x55 , 0x55 , 0xff , 0x55 , 0x55 , 0x55 , 0xff ,
                                        0x5f , 0x5f , 0x5f , 0xff , 0x5f , 0x5f , 0x5f , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x5f , 0x5f , 0x5f , 0xff ,
                                        0x92 , 0x92 , 0x92 , 0xff , 0x92 , 0x92 , 0x92 , 0xff ,
                                        0x92 , 0x92 , 0x92 , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x5f , 0x5f , 0x5f , 0xff , 0x92 , 0x92 , 0x92 , 0xff ,
                                        0x92 , 0x92 , 0x92 , 0xff , 0x92 , 0x92 , 0x92 , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x92 , 0x92 , 0x92 , 0xff , 0x92 , 0x92 , 0x92 , 0xff ,
                                        0x92 , 0x92 , 0x92 , 0xff , 0x92 , 0x92 , 0x92 , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x92 , 0x92 , 0x92 , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x5f , 0x5f , 0x5f , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x5f , 0x5f , 0x5f , 0xff , 0x5f , 0x5f , 0x5f , 0xff ,
                                        0x55 , 0x55 , 0x55 , 0xff , 0x55 , 0x55 , 0x55 , 0xff ,
                                        0x55 , 0x55 , 0x55 , 0xff , 0x55 , 0x55 , 0x55 , 0xff ,
                                        0x55 , 0x55 , 0x55 , 0xff , 0x5f , 0x5f , 0x5f , 0xff ,
                                        0x5f , 0x5f , 0x5f , 0xff , 0x5f , 0x5f , 0x5f , 0xff ,
                                        0x55 , 0x55 , 0x55 , 0xff , 0x55 , 0x55 , 0x55 , 0xff ,
                                        0x55 , 0x55 , 0x55 , 0xff , 0x55 , 0x55 , 0x55 , 0xff ,
                                        0x5f , 0x5f , 0x5f , 0xff , 0x5f , 0x5f , 0x5f , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x5f , 0x5f , 0x5f , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x92 , 0x92 , 0x92 , 0xff ,
                                        0x92 , 0x92 , 0x92 , 0xff , 0x92 , 0x92 , 0x92 , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x92 , 0x92 , 0x92 , 0xff , 0x92 , 0x92 , 0x92 , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x5f , 0x5f , 0x5f , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x92 , 0x92 , 0x92 , 0xff ,
                                        0x92 , 0x92 , 0x92 , 0xff , 0x92 , 0x92 , 0x92 , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x92 , 0x92 , 0x92 , 0xff , 0x92 , 0x92 , 0x92 , 0xff ,
                                        0x92 , 0x92 , 0x92 , 0xff , 0x92 , 0x92 , 0x92 , 0xff ,
                                        0x92 , 0x92 , 0x92 , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x5f , 0x5f , 0x5f , 0xff , 0x55 , 0x55 , 0x55 , 0xff ,
                                        0x5f , 0x5f , 0x5f , 0xff , 0x5f , 0x5f , 0x5f , 0xff ,
                                        0x55 , 0x55 , 0x55 , 0xff , 0x55 , 0x55 , 0x55 , 0xff ,
                                        0x55 , 0x55 , 0x55 , 0xff , 0x5f , 0x5f , 0x5f , 0xff ,
                                        0x5f , 0x5f , 0x5f , 0xff , 0x5f , 0x5f , 0x5f , 0xff ,
                                        0x5f , 0x5f , 0x5f , 0xff , 0x5f , 0x5f , 0x5f , 0xff ,
                                        0x55 , 0x55 , 0x55 , 0xff , 0x55 , 0x55 , 0x55 , 0xff ,
                                        0x55 , 0x55 , 0x55 , 0xff , 0x5f , 0x5f , 0x5f , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x5f , 0x5f , 0x5f , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x92 , 0x92 , 0x92 , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x92 , 0x92 , 0x92 , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x5f , 0x5f , 0x5f , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x92 , 0x92 , 0x92 , 0xff ,
                                        0x92 , 0x92 , 0x92 , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x92 , 0x92 , 0x92 , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x84 , 0x84 , 0x84 , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x84 , 0x84 , 0x84 , 0xff , 0x92 , 0x92 , 0x92 , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x9a , 0x9a , 0x9a , 0xff , 0x9a , 0x9a , 0x9a , 0xff ,
                                        0x5f , 0x5f , 0x5f , 0xff , 0x5f , 0x5f , 0x5f , 0xff ,
                                        0x5f , 0x5f , 0x5f , 0xff , 0x64 , 0x64 , 0x64 , 0xff ,
                                        0x55 , 0x55 , 0x55 , 0xff , 0x55 , 0x55 , 0x55 , 0xff ,
                                        0x64 , 0x64 , 0x64 , 0xff , 0x55 , 0x55 , 0x55 , 0xff ,
                                        0x5f , 0x5f , 0x5f , 0xff , 0x5f , 0x5f , 0x5f , 0xff ,
                                        0x5f , 0x5f , 0x5f , 0xff , 0x55 , 0x55 , 0x55 , 0xff ,
                                        0x3d , 0x3d , 0x3d , 0xff , 0x55 , 0x55 , 0x55 , 0xff ,
                                        0x55 , 0x55 , 0x55 , 0xff , 0x5f , 0x5f , 0x5f , 0xff };

        //--------------------------------------------------------------------------------------

            int h = 16;
            int w = 16;

            wooden = new int[h][w];

        //--------------------------------------------------------------------------------------
            for( int y = 0; y < h; y++ ) { for( int x = 0; x < w; x++ ) {
        //--------------------------------------------------------------------------------------

                int R = bytes[y * 16 * 4 + x * 4 + 0] & 255;
                int G = bytes[y * 16 * 4 + x * 4 + 1] & 255;
                int B = bytes[y * 16 * 4 + x * 4 + 2] & 255;
                int A = bytes[y * 16 * 4 + x * 4 + 3] & 255;

                wooden[y][x] = ( R << 24 ) | ( G << 16 ) | ( B << 8 ) | ( A );

        //--------------------------------------------------------------------------------------
            } }
        //--------------------------------------------------------------------------------------
        }

    //==========================================================================================

        @SubscribeEvent public static void forgeEnd( InitGuiEvent event ) {
        //--------------------------------------------------------------------------------------
            if( null != ForgeEndScreen) return;
        //--------------------------------------------------------------------------------------

            int w = Minecraft.getMinecraft().displayWidth;
            int h = Minecraft.getMinecraft().displayHeight;

            ForgeEndScreen = BufferUtils.createIntBuffer( w * h );

        //--------------------------------------------------------------------------------------

            int format = GL11.GL_RGBA;
            int type   = GL11.GL_UNSIGNED_BYTE;

            GL11.glReadPixels( 0 , 0 , w , h , format , type , ForgeEndScreen );

        //--------------------------------------------------------------------------------------
        }

    //==========================================================================================

        public static void Register() {
        //--------------------------------------------------------------------------------------

            Generate();

        //--------------------------------------------------------------------------------------
        }

        public static void Generate() {
        //--------------------------------------------------------------------------------------

            // fix the chest
            boolean darken = Configurations.getSettingsDarker();

        //──────────────────────────────────────────────────────────────────────────────────────
            for( Compressed compressed : ItemBlocks.entries ) {
        //--------------------------------------------------------------------------------------

                int[] incomplete = new int[compressed.items.size()];
                Block block = Block.getBlockFromItem( compressed.stack.getItem() );

                boolean isItem = Blocks.AIR.equals( block );

            //----------------------------------------------------------------------------------
                for(ItemX item: compressed.items) { for(EnumFacing face: EnumFacing.values()) {
            //----------------------------------------------------------------------------------

                    String side = "_" + face.getName();

                    String itemID = item.getRegistryName().getResourcePath();
                    Path   path   = ResourcePacks.Path( Type.TEXTURE , itemID + side );

                //------------------------------------------------------------------------------

                    incomplete[compressed.items.indexOf(item)] += Files.exists( path ) ? 0 : 1;

            //----------------------------------------------------------------------------------
                } }
            //----------------------------------------------------------------------------------

                if( 0 == IntStream.of( incomplete ).sum() ) continue;

            //──────────────────────────────────────────────────────────────────────────────────
                for( EnumFacing face : EnumFacing.values() ) {
            //----------------------------------------------------------------------------------


                    int[][] pixels = get2DTexData( compressed.stack , face );
                    //        pixels = edgePixels( averagePixel( pixels )  , pixels );

                    int[][] side   = colorPixels ( averagePixel( pixels ) , wooden , isItem );

                //------------------------------------------------------------------------------

                    int avg1 = averagePixel( pixels );

                    if( Blocks.AIR.equals( block ) || 255 != ( avg1 & 255 ) ) {

                        int avg2 = averagePixel( side );

                        float[] HSB1 = new float[3];

                        int R = ( avg1 >> 24 ) & 255;
                        int G = ( avg1 >> 16 ) & 255;
                        int B = ( avg1 >> 8  ) & 255;

                        Color.RGBtoHSB( R , G , B , HSB1 );

                        float[] HSB2 = new float[3];

                        R = ( avg2 >> 24 ) & 255;
                        G = ( avg2 >> 16 ) & 255;
                        B = ( avg2 >> 8  ) & 255;

                        Color.RGBtoHSB( R , G , B , HSB2 );

                        float diffSA = Math.abs( HSB1[1] - HSB2[1] );
                        float diffBR = Math.abs( HSB1[2] - HSB2[2] );

                        if( diffBR < 0.2 && diffSA < 0.2 ) {

                            int h = 16;
                            int w = 16;


                            for( int y = 0; y < h; y++ ) { for( int x = 0; x < w; x++ ) {

                                int pixel = pixels[y][x];
                                int A     = pixel & 255;

                                float[] HSB = new float[3];

                                R = ( pixel >> 24 ) & 255;
                                G = ( pixel >> 16 ) & 255;
                                B = ( pixel >> 8  ) & 255;

                                Color.RGBtoHSB( R , G , B , HSB );

                                HSB[1] = (float) Math.sqrt( HSB[1] );
                                HSB[2] = (float) Math.sqrt( HSB[2] );

                                Color hued = new Color( Color.HSBtoRGB(HSB[0], HSB[1], HSB[2]));

                                R = hued.getRed();
                                G = hued.getGreen();
                                B = hued.getBlue();

                            //------------------------------------------------------------------

                                pixels[y][x] = ( R << 24 ) | ( G << 16 ) | ( B << 8 ) | ( A );
                            }}
                        }
                    }

                //------------------------------------------------------------------------------

                    Item base = compressed.stack .getItem();
                    String name = base.getRegistryName().getResourcePath();

                    Boolean transparent = false;

                    transparent = transparent ||  name.contains( "glass" );
                    transparent = transparent ||  name.contains( "ice" );
                    transparent = transparent && !name.contains( "bottle" );

                    int[][] back = transparent ? frame : joinPixels( side , frame );

                //------------------------------------------------------------------------------

                    int[][] joined = joinPixels( back , pixels );

                //------------------------------------------------------------------------------
                    for( ItemX item : compressed.items ) {
                //------------------------------------------------------------------------------

                        String  itemSide = "_" + face.getName();
                        String  itemID   = item.getRegistryName().getResourcePath();

                        int[][] darkened = darken ? darkenPixels(item.level , joined) : joined;

                        ResourcePacks.Write( darkened , Type.TEXTURE , itemID + itemSide );

                //------------------------------------------------------------------------------
                    }
                //------------------------------------------------------------------------------



            //----------------------------------------------------------------------------------
                }
            //----------------------------------------------------------------------------------

        } }

    //==========================================================================================
    // Manipulate a single pixel
    //==========================================================================================

        public static int averagePixel( int[][] pixels ) {
        //--------------------------------------------------------------------------------------

            int h = 16;
            int w = 16;

        //--------------------------------------------------------------------------------------

            int R = 0;
            int G = 0;
            int B = 0;
            int A = 0;

            int countRGB = 0;
            int countA = 0;

        //--------------------------------------------------------------------------------------
            for( int y = 0; y < h; y++ ) { for( int x = 0; x < w; x++ ) {
        //--------------------------------------------------------------------------------------

                int r = ( pixels[y][x] >> 24 ) & 255;
                int g = ( pixels[y][x] >> 16 ) & 255;
                int b = ( pixels[y][x] >> 8  ) & 255;
                int a = ( pixels[y][x]       ) & 255;


                if( a != 0 ) R += r;
                if( a != 0 ) G += g;
                if( a != 0 ) B += b;
                A += a;

                if( a != 0 ) countRGB++;
                countA++;

        //--------------------------------------------------------------------------------------
            } }
        //--------------------------------------------------------------------------------------

            R = 0 != countRGB ? (int) ( ( R / countRGB ) * 1.0 ) : 0;
            G = 0 != countRGB ? (int) ( ( G / countRGB ) * 1.0 ) : 0;
            B = 0 != countRGB ? (int) ( ( B / countRGB ) * 1.0 ) : 0;
            A = (int) ( ( A / countA ) * 1.0 );

            R = R > 255 ? 255 : R;
            G = G > 255 ? 255 : G;
            B = B > 255 ? 255 : B;
            A = A > 255 ? 255 : A;

            return ( R << 24 ) | ( G << 16 ) | ( B << 8 ) | ( A );

        //--------------------------------------------------------------------------------------
        }

        public static int darkenPixel ( int color ) {
        //--------------------------------------------------------------------------------------

            int darker = 0;

        //--------------------------------------------------------------------------------------

            darker += ( (int) ( ( ( color >> 24 ) & 255 ) * 0.7 ) ) << 24;
            darker += ( (int) ( ( ( color >> 16 ) & 255 ) * 0.7 ) ) << 16;
            darker += ( (int) ( ( ( color >> 8  ) & 255 ) * 0.7 ) ) << 8 ;
            darker += ( (int) ( ( ( color >> 0  ) & 255 ) * 1.0 ) ) << 0 ;

        //--------------------------------------------------------------------------------------

            return darker;

        //--------------------------------------------------------------------------------------
        }

        public static int getPixelHue ( int color ) {
        //--------------------------------------------------------------------------------------

            int R = ( color >> 24 ) & 255;
            int G = ( color >> 16 ) & 255;
            int B = ( color >> 8  ) & 255;

        //--------------------------------------------------------------------------------------

            float min = R;

            if( R <= G && R <= B ) min = R;
            if( G <= B && G <= R ) min = G;
            if( B <= R && B <= G ) min = B;

        //--------------------------------------------------------------------------------------

            float max = R;

            if( R >= G && R >= B ) max = R;
            if( G >= B && G >= R ) max = G;
            if( B >= R && B >= G ) max = B;

        //--------------------------------------------------------------------------------------

            if( max == min ) return 0;

        //--------------------------------------------------------------------------------------

            float hue = 0f;

            if ( max == R ) { hue = 0f + ( G - B ) / ( max - min ); }
            if ( max == G ) { hue = 2f + ( B - R ) / ( max - min ); }
            if ( max == B ) { hue = 4f + ( R - G ) / ( max - min ); }

        //--------------------------------------------------------------------------------------

            hue = hue * 60;
            if( hue < 0 ) hue = hue + 360;

        //--------------------------------------------------------------------------------------
            return Math.round( hue );
        //--------------------------------------------------------------------------------------
        }

    //==========================================================================================

        public static double[] Bounds( ItemStack stack ) {
        //--------------------------------------------------------------------------------------
            double[] bounds = new double[]{ 10 , 10 , 10 , -10 , -10 , -10 };
        //--------------------------------------------------------------------------------------

            Block       block = Block.getBlockFromItem( stack.getItem() );
            IBlockState state = block.getBlockState().getBaseState();

        //--------------------------------------------------------------------------------------
            try {
        //--------------------------------------------------------------------------------------

                AxisAlignedBB box = state.getBoundingBox( null , null );

                bounds[0] = box.minX;  bounds[1] = box.minY; bounds[2] = box.minZ;
                bounds[3] = box.maxX;  bounds[4] = box.maxY; bounds[5] = box.maxZ;

                return bounds;

        //--------------------------------------------------------------------------------------
            } catch( NullPointerException ex ) {  } try {
        //--------------------------------------------------------------------------------------

                AxisAlignedBB box = state.getBoundingBox( null , null );

                bounds[0] = box.minX;  bounds[1] = box.minY; bounds[2] = box.minZ;
                bounds[3] = box.maxX;  bounds[4] = box.maxY; bounds[5] = box.maxZ;

                return bounds;

        //--------------------------------------------------------------------------------------
            } catch( NullPointerException ex ) {  }
        //--------------------------------------------------------------------------------------

            IBakedModel model = Minecraft.getMinecraft()
                    .getRenderItem()
                    .getItemModelMesher()
                    .getItemModel( stack );

        //--------------------------------------------------------------------------------------

            EnumFacing[] sides = new EnumFacing[] { EnumFacing.NORTH
                                                  , EnumFacing.WEST
                                                  , EnumFacing.SOUTH
                                                  , EnumFacing.EAST };


        //--------------------------------------------------------------------------------------
            for(EnumFacing side: sides) { for(BakedQuad quad: model.getQuads(state, side, 0)) {
        //--------------------------------------------------------------------------------------

                float x0 = Float.intBitsToFloat( quad.getVertexData()[0 + 7 * 0] );
                float y0 = Float.intBitsToFloat( quad.getVertexData()[1 + 7 * 0] );
                float z0 = Float.intBitsToFloat( quad.getVertexData()[2 + 7 * 0] );

                float x1 = Float.intBitsToFloat( quad.getVertexData()[0 + 7 * 1] );
                float y1 = Float.intBitsToFloat( quad.getVertexData()[1 + 7 * 1] );
                float z1 = Float.intBitsToFloat( quad.getVertexData()[2 + 7 * 1] );

                float x2 = Float.intBitsToFloat( quad.getVertexData()[0 + 7 * 2] );
                float y2 = Float.intBitsToFloat( quad.getVertexData()[1 + 7 * 2] );
                float z2 = Float.intBitsToFloat( quad.getVertexData()[2 + 7 * 2] );

                float x3 = Float.intBitsToFloat( quad.getVertexData()[0 + 7 * 3] );
                float y3 = Float.intBitsToFloat( quad.getVertexData()[1 + 7 * 3] );
                float z3 = Float.intBitsToFloat( quad.getVertexData()[2 + 7 * 3] );

            //----------------------------------------------------------------------------------

                if( x0 != x1 || x0 != x2 || x0 != x3 ) if( x0 < bounds[0] ) bounds[0] = x0;
                if( x0 != x1 || x0 != x2 || x0 != x3 ) if( x1 < bounds[0] ) bounds[0] = x1;
                if( x0 != x1 || x0 != x2 || x0 != x3 ) if( x2 < bounds[0] ) bounds[0] = x2;
                if( x0 != x1 || x0 != x2 || x0 != x3 ) if( x3 < bounds[0] ) bounds[0] = x3;

                if( y0 != y1 || y0 != y2 || y0 != y3 ) if( y0 < bounds[1] ) bounds[1] = y0;
                if( y0 != y1 || y0 != y2 || y0 != y3 ) if( y1 < bounds[1] ) bounds[1] = y1;
                if( y0 != y1 || y0 != y2 || y0 != y3 ) if( y2 < bounds[1] ) bounds[1] = y2;
                if( y0 != y1 || y0 != y2 || y0 != y3 ) if( y3 < bounds[1] ) bounds[1] = y3;

                if( z0 != z1 || z0 != z2 || z0 != z3 ) if( z0 < bounds[2] ) bounds[2] = z0;
                if( z0 != z1 || z0 != z2 || z0 != z3 ) if( z1 < bounds[2] ) bounds[2] = z1;
                if( z0 != z1 || z0 != z2 || z0 != z3 ) if( z2 < bounds[2] ) bounds[2] = z2;
                if( z0 != z1 || z0 != z2 || z0 != z3 ) if( z3 < bounds[2] ) bounds[2] = z3;

                if( x0 != x1 || x0 != x2 || x0 != x3 ) if( x0 > bounds[3] ) bounds[3] = x0;
                if( x0 != x1 || x0 != x2 || x0 != x3 ) if( x1 > bounds[3] ) bounds[3] = x1;
                if( x0 != x1 || x0 != x2 || x0 != x3 ) if( x2 > bounds[3] ) bounds[3] = x2;
                if( x0 != x1 || x0 != x2 || x0 != x3 ) if( x3 > bounds[3] ) bounds[3] = x3;

                if( y0 != y1 || y0 != y2 || y0 != y3 ) if( y0 > bounds[4] ) bounds[4] = y0;
                if( y0 != y1 || y0 != y2 || y0 != y3 ) if( y1 > bounds[4] ) bounds[4] = y1;
                if( y0 != y1 || y0 != y2 || y0 != y3 ) if( y2 > bounds[4] ) bounds[4] = y2;
                if( y0 != y1 || y0 != y2 || y0 != y3 ) if( y3 > bounds[4] ) bounds[4] = y3;

                if( z0 != z1 || z0 != z2 || z0 != z3 ) if( z0 > bounds[5] ) bounds[5] = z0;
                if( z0 != z1 || z0 != z2 || z0 != z3 ) if( z1 > bounds[5] ) bounds[5] = z1;
                if( z0 != z1 || z0 != z2 || z0 != z3 ) if( z2 > bounds[5] ) bounds[5] = z2;
                if( z0 != z1 || z0 != z2 || z0 != z3 ) if( z3 > bounds[5] ) bounds[5] = z3;

        //--------------------------------------------------------------------------------------
            } } return bounds;
        //--------------------------------------------------------------------------------------
        }

    //==========================================================================================
    // Get a lot of pixels
    //==========================================================================================

        public static Boolean rotate = true;

        public static int[][] getFileData ( FileSystem mod , String name ) { try {
        //--------------------------------------------------------------------------------------

            String texLoc = null != mod ? "" : Base.root + "/../src/main/resources/";

            texLoc += "utility/" + name + ".png";

        //--------------------------------------------------------------------------------------

            Path path = null != mod ? mod.getPath( texLoc ): Paths.get( texLoc );

            if( !Files.exists( path ) ) return new int[1][1];

        //--------------------------------------------------------------------------------------

            InputStream input = Files.newInputStream( path );

            BufferedImage image = ImageIO.read( input );

            input.close();

        //--------------------------------------------------------------------------------------

            int h = 16;
            int w = 16;

            int[][] data = new int[h][w];

        //--------------------------------------------------------------------------------------
            for( int y = 0; y < h; y++ ) { for( int x = 0; x < w; x++ ) {
        //--------------------------------------------------------------------------------------

                int A = ( image.getRGB( x , y ) >> 24 ) & 255;
                int R = ( image.getRGB( x , y ) >> 16 ) & 255;
                int G = ( image.getRGB( x , y ) >> 8  ) & 255;
                int B = ( image.getRGB( x , y )       ) & 255;

                data[y][x] = ( R << 24 ) | ( G << 16 ) | ( B << 8 ) | ( A );

        //--------------------------------------------------------------------------------------
            } }
        //--------------------------------------------------------------------------------------

            return data;

        //--------------------------------------------------------------------------------------
        } catch ( IOException e ) { e.printStackTrace(); return new int[1][1]; } }

        public static int[][] get2DTexData( ItemStack stack , EnumFacing face ) {
        //--------------------------------------------------------------------------------------

            IBakedModel model = Minecraft.getMinecraft()
                    .getRenderItem()
                    .getItemModelMesher()
                    .getItemModel( stack );

            model = model.getOverrides().handleItemState( model , stack , null , null );
            model = ForgeHooksClient.handleCameraTransforms( model ,
                    ItemCameraTransforms.TransformType.GUI , false );

        //--------------------------------------------------------------------------------------

            int w = 16;
            int h = 16;

            Framebuffer frameBuffer = new Framebuffer( w , h , true );

            frameBuffer.setFramebufferColor( 0 , 0 , 0 , 0 );
            frameBuffer.framebufferClear();

            frameBuffer.bindFramebuffer( true );

        //--------------------------------------------------------------------------------------

            GL11.glPushAttrib( GL11.GL_ALL_ATTRIB_BITS );

            GL11.glMatrixMode( GL11.GL_MODELVIEW );
            GL11.glPushMatrix();

            GL11.glMatrixMode( GL11.GL_PROJECTION );
            GL11.glPushMatrix();

            GL11.glMatrixMode( GL11.GL_TEXTURE );
            GL11.glPushMatrix();

            GL11.glMatrixMode( GL11.GL_COLOR );
            GL11.glPushMatrix();

        //--------------------------------------------------------------------------------------

            GL11.glShadeModel( GL11.GL_SMOOTH );
            GL11.glHint( GL11.GL_PERSPECTIVE_CORRECTION_HINT , GL11.GL_NICEST );

            GL11.glClearDepth( 1.0 );
            GL11.glEnable( GL11.GL_DEPTH_TEST );
            GL11.glDepthFunc( GL11.GL_LEQUAL );

            GL11.glDisable( GL11.GL_LIGHTING );
            GL11.glEnable( GL11.GL_COLOR_MATERIAL );

            GL11.glEnable( GL11.GL_ALPHA_TEST );
            GL11.glAlphaFunc( GL11.GL_GREATER , 0.1F );

            GL11.glEnable( GL11.GL_TEXTURE_2D );

        //--------------------------------------------------------------------------------------

            GL11.glMatrixMode( GL11.GL_PROJECTION );
            GL11.glLoadIdentity();

            GL11.glOrtho( -0.5 , 0.5 , -0.5 , 0.5 , -1.0 , 1.0 );
            GL11.glViewport( 0 , 0 , w , h );

            GL11.glMatrixMode( GL11.GL_MODELVIEW );
            GL11.glLoadIdentity();

        //--------------------------------------------------------------------------------------

            if( face.equals( EnumFacing.DOWN ) )
                GL11.glRotatef( 180.0F , 0.0F , 0.0F , 1.0F );

        //--------------------------------------------------------------------------------------

            Boolean itemBlock = stack.getItem() instanceof ItemBlock;

            //Block block = Block.getBlockFromItem( stack.getItem() );

            ResourceLocation loc = stack.getItem().getRegistryName();

            //boolean chest = loc.getResourcePath().equals( "chest" );
            boolean bed   = stack.getItem() instanceof ItemBed;

        //--------------------------------------------------------------------------------------
            if( itemBlock || bed ) { try {
        //--------------------------------------------------------------------------------------

                if( loc.getResourcePath().equals( "chest" )
                ||  loc.getResourcePath().equals( "trapped_chest" )  ) {
                    if( !face.equals( EnumFacing.DOWN ) &&
                        !face.equals( EnumFacing.UP ) )
                            GL11.glRotatef( 180.0F , 0.0F , 1.0F , 0.0F );

                    if( face.equals( EnumFacing.DOWN ) ||
                        face.equals( EnumFacing.UP ) )
                            GL11.glRotatef( 180.0F , 0.0F , 0.0F , 1.0F );
                }

                if(  loc.getResourcePath().contains( "fence" )
                &&  !loc.getResourcePath().contains( "gate" ) ) {
                    if( !face.equals( EnumFacing.DOWN ) &&
                        !face.equals( EnumFacing.UP ) )
                            GL11.glRotatef( 90.0F , 0.0F , 1.0F , 0.0F );

                    if( face.equals( EnumFacing.DOWN ) ||
                        face.equals( EnumFacing.UP ) )
                            GL11.glRotatef( 90.0F , 0.0F , 0.0F , 1.0F );
                }

                if( loc.getResourcePath().contains( "bed" ) ) {
                    if( face.equals( EnumFacing.SOUTH ) ||
                        face.equals( EnumFacing.NORTH ) )
                        GL11.glTranslatef( 0.0F , 0.0F , -1.0F );
                }

                double[] bounds = Bounds( stack );

                double height = bounds[4] - bounds[1];
                if( height < 0 ) height = 1.0f;

            //----------------------------------------------------------------------------------

                if( height >= 0 && height < 0.5f && rotate )
                    GL11.glRotatef( 90.0F , 1.0F , 0.0F , 0.0F );

            //----------------------------------------------------------------------------------
                if( height >= 0.5f ) {
            //----------------------------------------------------------------------------------

                    if( face.equals( EnumFacing.DOWN ) ) {
                        GL11.glRotatef( -90.0F , 1.0F , 0.0F , 0.0F );
                        GL11.glRotatef( 180.0F , 0.0F , 1.0F , 0.0F );
                    }

                    if( face.equals( EnumFacing.UP ) ) // 1
                        GL11.glRotatef( +90.0F , 1.0F , 0.0F , 0.0F );

                    if( face.equals( EnumFacing.NORTH ) ) // 6
                        GL11.glRotatef( 180.0F , 0.0F , 1.0F , 0.0F );

                    if( face.equals( EnumFacing.SOUTH ) ) // 3
                        GL11.glRotatef(   0.0F , 0.0F , 1.0F , 0.0F );

                    if( face.equals( EnumFacing.WEST ) )
                        GL11.glRotatef( +90.0F , 0.0F , 1.0F , 0.0F );

                    if( face.equals( EnumFacing.EAST ) )
                        GL11.glRotatef( -90.0F , 0.0F , 1.0F , 0.0F );

            //----------------------------------------------------------------------------------
                }
        //--------------------------------------------------------------------------------------
            } catch( NullPointerException ex ) { int s = 0; } }
        //--------------------------------------------------------------------------------------

        //--------------------------------------------------------------------------------------
            if( null != loc ) {
        //--------------------------------------------------------------------------------------

                boolean shield = loc.getResourcePath().contains( "shield" );

                //if( bed )    GL11.glRotatef( +90.0F , 0.0F , 1.0F , 0.0F );

                if( shield ) GL11.glScalef( 0.6F , 0.6F , 0.6F );
                if( shield ) GL11.glTranslatef( 0.5F , 0.5F , 1.0F );

        //--------------------------------------------------------------------------------------
            }
        //--------------------------------------------------------------------------------------

            TextureManager texMan = Minecraft.getMinecraft().getTextureManager();
            texMan.bindTexture( TextureMap.LOCATION_BLOCKS_TEXTURE );

            Minecraft minecraft = Minecraft.getMinecraft();
            RenderItem renderItem = minecraft.getRenderItem();
            renderItem.renderItem( stack , model );

            Display.update();

        //--------------------------------------------------------------------------------------

            IntBuffer buff = BufferUtils.createIntBuffer( w * h );
            GL11.glReadPixels( 0, 0, w, h, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buff );

        //--------------------------------------------------------------------------------------

            GL11.glMatrixMode( GL11.GL_COLOR );
            GL11.glPopMatrix();

            GL11.glMatrixMode( GL11.GL_TEXTURE );
            GL11.glPopMatrix();

            GL11.glMatrixMode( GL11.GL_PROJECTION );
            GL11.glPopMatrix();

            GL11.glMatrixMode( GL11.GL_MODELVIEW );
            GL11.glPopMatrix();

            GL11.glPopAttrib();

        //--------------------------------------------------------------------------------------

            int[][] data = new int[h][w];

        //--------------------------------------------------------------------------------------
            for( int y = 0; y < h; y++ ) { for( int x = 0; x < w; x++ ) {
        //--------------------------------------------------------------------------------------

                int A = ( buff.get( y * w + x ) >> 24 ) & 255;
                int B = ( buff.get( y * w + x ) >> 16 ) & 255;
                int G = ( buff.get( y * w + x ) >> 8  ) & 255;
                int R = ( buff.get( y * w + x )       ) & 255;

                data[h - 1 - y][x] = ( R << 24 ) | ( G << 16 ) | ( B << 8 ) | ( A );

        //--------------------------------------------------------------------------------------
            } }
        //--------------------------------------------------------------------------------------

            frameBuffer.unbindFramebufferTexture();
            frameBuffer.unbindFramebuffer();
            frameBuffer.deleteFramebuffer();

        //--------------------------------------------------------------------------------------
        // Capturing frames in the framebuffer causes black frames to show
        //--------------------------------------------------------------------------------------

            w = Minecraft.getMinecraft().displayWidth;
            h = Minecraft.getMinecraft().displayHeight;

            int format = GL11.GL_RGBA;
            int type   = GL11.GL_UNSIGNED_BYTE;

            GL11.glDrawPixels( w , h , format , type , ForgeEndScreen );

            Minecraft.getMinecraft().updateDisplay();

        //--------------------------------------------------------------------------------------
        // Crude fix for rails
        //--------------------------------------------------------------------------------------

            Boolean empty = ( 0 == ( averagePixel( data ) & 255 ) );

        //--------------------------------------------------------------------------------------

            if( empty && face == EnumFacing.SOUTH && rotate == false ) return data;

        //--------------------------------------------------------------------------------------

            if( empty && face != EnumFacing.SOUTH ) data = get2DTexData(stack,EnumFacing.SOUTH);

        //--------------------------------------------------------------------------------------

            if( empty && face == EnumFacing.SOUTH ) rotate = false;
            if( empty && face == EnumFacing.SOUTH ) data = get2DTexData( stack , face );
            if( empty && face == EnumFacing.SOUTH ) rotate = true;

        //--------------------------------------------------------------------------------------

            return data;

        //--------------------------------------------------------------------------------------
        }

        public static int[][] get3DTexData( ItemStack stack ) {
        //--------------------------------------------------------------------------------------

            IBakedModel model = Minecraft.getMinecraft()
                    .getRenderItem()
                    .getItemModelMesher()
                    .getItemModel( stack );

            model = model.getOverrides().handleItemState( model , stack , null , null );

        //--------------------------------------------------------------------------------------

            int w = w3D;
            int h = h3D;

            Framebuffer frameBuffer = new Framebuffer( w , h , true );

            frameBuffer.setFramebufferColor( 0 , 0 , 0 , 0 );
            frameBuffer.framebufferClear();

            frameBuffer.bindFramebuffer( true );

        //--------------------------------------------------------------------------------------

            GL11.glPushAttrib( GL11.GL_ALL_ATTRIB_BITS );

            GL11.glMatrixMode( GL11.GL_MODELVIEW );
            GL11.glPushMatrix();

            GL11.glMatrixMode( GL11.GL_PROJECTION );
            GL11.glPushMatrix();

            GL11.glMatrixMode( GL11.GL_TEXTURE );
            GL11.glPushMatrix();

            GL11.glMatrixMode( GL11.GL_COLOR );
            GL11.glPushMatrix();

        //--------------------------------------------------------------------------------------

            GL11.glShadeModel( GL11.GL_SMOOTH );

            GL11.glClearDepth( 1.0 );
            GL11.glEnable( GL11.GL_DEPTH_TEST );
            GL11.glDepthFunc( GL11.GL_LEQUAL );

            GL11.glEnable( GL11.GL_LIGHTING );
            GL11.glEnable( GL11.GL_LIGHT1 );
            GL11.glLightf( GL11.GL_LIGHT1 , GL11.GL_LINEAR_ATTENUATION , 0.5f );

            GL11.glEnable( GL11.GL_COLOR_MATERIAL );
            GL11.glHint( GL11.GL_PERSPECTIVE_CORRECTION_HINT , GL11.GL_NICEST );

            GL11.glEnable( GL11.GL_ALPHA_TEST );
            GL11.glAlphaFunc( GL11.GL_GREATER , 0.1F );

            GL11.glEnable( GL11.GL_TEXTURE_2D );

            GL11.glEnable( GL11.GL_CULL_FACE );
            GL11.glCullFace( GL11.GL_FRONT );

        //--------------------------------------------------------------------------------------

            GL11.glMatrixMode( GL11.GL_PROJECTION );
            GL11.glLoadIdentity();

            GL11.glFrustum( -1.0 , 1.0 , -1.0 , 1.0 , -1.0 , 1.0 );
            GL11.glViewport( 0 , 0 , w , h );

            GL11.glMatrixMode( GL11.GL_MODELVIEW );
            GL11.glLoadIdentity();

        //--------------------------------------------------------------------------------------

            FloatBuffer lightPos = BufferUtils.createFloatBuffer( 4 );
            lightPos.put( new float[] { -1f , 2f , -2f , 1f } );
            lightPos.flip();

            FloatBuffer LightDiffuse = BufferUtils.createFloatBuffer(4);
            LightDiffuse.put( new float[]{ 1f , 1f , 1f , 0f } );
            LightDiffuse.flip();

            GL11.glLight(  GL11.GL_LIGHT1 , GL11.GL_DIFFUSE , LightDiffuse );
            GL11.glLight(  GL11.GL_LIGHT1 , GL11.GL_POSITION , lightPos );
            GL11.glLightf( GL11.GL_LIGHT1 , GL11.GL_LINEAR_ATTENUATION , 0.9f );

        //--------------------------------------------------------------------------------------

            GL11.glRotatef( -25.0F , 1.0F , 0.0F , 0.0F );
            GL11.glRotatef( -45.0F , 0.0F , 1.0F , 0.0F );

            TextureManager texMan = Minecraft.getMinecraft().getTextureManager();
            texMan.bindTexture( TextureMap.LOCATION_BLOCKS_TEXTURE );

            Minecraft minecraft = Minecraft.getMinecraft();
            RenderItem renderItem = minecraft.getRenderItem();
            renderItem.renderItem( stack , model );

            Display.update();

        //--------------------------------------------------------------------------------------

            IntBuffer buff = BufferUtils.createIntBuffer( w * h );
            GL11.glReadPixels( 0, 0, w, h, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buff );

        //--------------------------------------------------------------------------------------

            GL11.glMatrixMode( GL11.GL_COLOR );
            GL11.glPopMatrix();

            GL11.glMatrixMode( GL11.GL_TEXTURE );
            GL11.glPopMatrix();

            GL11.glMatrixMode( GL11.GL_PROJECTION );
            GL11.glPopMatrix();

            GL11.glMatrixMode( GL11.GL_MODELVIEW );
            GL11.glPopMatrix();

            GL11.glPopAttrib();

        //--------------------------------------------------------------------------------------

            int[][] data = new int[h][w];

        //--------------------------------------------------------------------------------------
            for( int y = 0; y < h; y++ ) { for( int x = 0; x < w; x++ ) {
        //--------------------------------------------------------------------------------------

                int A = ( buff.get( y * w + x ) >> 24 ) & 255;
                int B = ( buff.get( y * w + x ) >> 16 ) & 255;
                int G = ( buff.get( y * w + x ) >> 8  ) & 255;
                int R = ( buff.get( y * w + x )       ) & 255;

                data[h - 1 - y][x] = ( R << 24 ) | ( G << 16 ) | ( B << 8 ) | ( A );

        //--------------------------------------------------------------------------------------
            } }
        //--------------------------------------------------------------------------------------

            frameBuffer.unbindFramebufferTexture();
            frameBuffer.unbindFramebuffer();
            frameBuffer.deleteFramebuffer();

        //--------------------------------------------------------------------------------------

            return data;

        //--------------------------------------------------------------------------------------
        }

    //==========================================================================================
    // Manipulate a lot of pixels
    //==========================================================================================

        public static int[][] darkenPixels( int step      , int[][] pixels ) {
        //--------------------------------------------------------------------------------------

            int h = 16;
            int w = 16;

        //--------------------------------------------------------------------------------------
            for( int y = 0; y < step && y < h; y++ ) { for( int x =1+y-1; x<w-y; x++ ){
        //--------------------------------------------------------------------------------------

                int end = h - y - 1;

                for(int i = 0; i < step - y; i++) pixels[ y ][x] = darkenPixel(pixels[ y ][x]);
                for(int i = 0; i < step - y; i++) pixels[end][x] = darkenPixel(pixels[end][x]);

        //--------------------------------------------------------------------------------------
            } } for(int x=0; x < step && x < w; x++) { for(int y=1+x; y < h-x-1; y++){
        //--------------------------------------------------------------------------------------

                int end = h - x - 1;

                for(int i = 0; i < step - x; i++) pixels[y][ x ] = darkenPixel(pixels[y][ x ]);
                for(int i = 0; i < step - x; i++) pixels[y][end] = darkenPixel(pixels[y][end]);

        //--------------------------------------------------------------------------------------
            } }
        //--------------------------------------------------------------------------------------

            return pixels;

        //--------------------------------------------------------------------------------------
        }

        public static int[][] joinPixels  ( int[][] under , int[][] above  ) {
        //--------------------------------------------------------------------------------------

            int h = 16;
            int w = 16;

            int[][] joined = new int[h][w];

        //--------------------------------------------------------------------------------------
            for( int y = 0; y < h; y++ ) { for( int x = 0; x < w; x++ ) {
        //--------------------------------------------------------------------------------------

                int uR = ( under[y][x] >> 24 ) & 255;
                int uG = ( under[y][x] >> 16 ) & 255;
                int uB = ( under[y][x] >> 8  ) & 255;
                int uA = ( under[y][x]       ) & 255;

            //----------------------------------------------------------------------------------

                int aR = ( above[y][x] >> 24 ) & 255;
                int aG = ( above[y][x] >> 16 ) & 255;
                int aB = ( above[y][x] >> 8  ) & 255;
                int aA = ( above[y][x]       ) & 255;

            //----------------------------------------------------------------------------------

                final int A = 255 - ( ( 255 - aA ) * ( 255 - uA ) ) / 255;

            //----------------------------------------------------------------------------------

                if( 0 == A ) joined[y][x] = 0;
                if( 0 == A ) continue;

            //----------------------------------------------------------------------------------

                int R = ( ( ( 255 * aR * aA ) + ( uR * uA * ( 255 - aA ) ) ) / A ) / 255;
                int G = ( ( ( 255 * aG * aA ) + ( uG * uA * ( 255 - aA ) ) ) / A ) / 255;
                int B = ( ( ( 255 * aB * aA ) + ( uB * uA * ( 255 - aA ) ) ) / A ) / 255;

                joined[y][x] = ( R << 24 ) | ( G << 16 ) | ( B << 8 ) | ( A );

        //--------------------------------------------------------------------------------------
            } }
        //--------------------------------------------------------------------------------------

            return joined;

        //--------------------------------------------------------------------------------------
        }

        public static int[][] colorPixels ( int color     , int[][] pixels, boolean item ) {
        //--------------------------------------------------------------------------------------

            float hue = ( getPixelHue( color ) ) * 1.0f / 360;

            int average = averagePixel( pixels );

        //--------------------------------------------------------------------------------------

            int h = 16;
            int w = 16;

            int[][] joined = new int[h][w];

        //--------------------------------------------------------------------------------------

            float[] cHSB = new float[3];

            int cR = ( color >> 24 ) & 255;
            int cG = ( color >> 16 ) & 255;
            int cB = ( color >> 8  ) & 255;
            int cA = ( color >> 0  ) & 255;

            Color.RGBtoHSB( cR , cG , cB , cHSB );

            float[] aHSB = new float[3];

            int aR = ( average >> 24 ) & 255;
            int aG = ( average >> 16 ) & 255;
            int aB = ( average >> 8  ) & 255;

            Color.RGBtoHSB( aR , aG , aB , aHSB );

        //--------------------------------------------------------------------------------------
            for( int y = 0; y < h; y++ ) { for( int x = 0; x < w; x++ ) {
        //--------------------------------------------------------------------------------------

                float[] HSB = new float[3];

                int R = ( pixels[y][x] >> 24 ) & 255;
                int G = ( pixels[y][x] >> 16 ) & 255;
                int B = ( pixels[y][x] >> 8  ) & 255;
                int A = ( pixels[y][x]       ) & 255;

                Color.RGBtoHSB( R , G , B , HSB );

            //----------------------------------------------------------------------------------

                float brightness = HSB[2] * HSB[2];
                //float brightness = HSB[2] / cHSB[2];

                //if( Math.abs( HSB[2] * HSB[2] - cHSB[2] ) < 0.25 )
                //    brightness = 1.0f - brightness;

                //if( 0.0 <= cHSB[2] && cHSB[2] <= 0.5 ) brightness = 1.0f - brightness;
                //if( 0.5 <= cHSB[2] && cHSB[2] <= 1.0 ) brightness = brightness;

                if( !item && cA > 110 ) if( 0.0 <= cHSB[2] && cHSB[2] <= 0.4 )
                    brightness = (float) Math.sqrt( HSB[2] );

                //if( 0.20 <= cHSB[2] && cHSB[2] <= 0.45 ) brightness = HSB[2] * HSB[2] *
                // HSB[2];
                //if( 0.45 <= cHSB[2] && cHSB[2] <= 0.75 ) brightness = HSB[2] * HSB[2];
                //if( 0.75 <= cHSB[2] && cHSB[2] <= 1.0 ) brightness = HSB[2];

                Color hued = new Color( Color.HSBtoRGB( hue , cHSB[1] , brightness ) );

            //----------------------------------------------------------------------------------

                R = hued.getRed();
                G = hued.getGreen();
                B = hued.getBlue();

            //----------------------------------------------------------------------------------

                joined[y][x] = ( R << 24 ) | ( G << 16 ) | ( B << 8 ) | ( A );

        //--------------------------------------------------------------------------------------
            } }
        //--------------------------------------------------------------------------------------

            return joined;

        //--------------------------------------------------------------------------------------
        }

        public static int[][] edgePixels  ( int color     , int[][] pixels ) {
        //--------------------------------------------------------------------------------------

            float[] HSB = new float[3];

            int R = ( color >> 24 ) & 255;
            int G = ( color >> 16 ) & 255;
            int B = ( color >> 8  ) & 255;
            int A = ( color       ) & 255;

            Color.RGBtoHSB( R , G , B , HSB );

        //--------------------------------------------------------------------------------------

            HSB[2] = ( HSB[2] + 0.35f ) > 1.0f ? 1.0f : HSB[2] + 0.35f;
            HSB[2] = 1.0f;
            A = 125;

            Color brightened = new Color( Color.HSBtoRGB( HSB[0] , HSB[1] , HSB[2] ) );

        //--------------------------------------------------------------------------------------

            R = brightened.getRed();
            G = brightened.getGreen();
            B = brightened.getBlue();

            color = ( R << 24 ) | ( G << 16 ) | ( B << 8 ) | ( A );

        //--------------------------------------------------------------------------------------

            int h = 16;
            int w = 16;

            int[][] edged = new int[h][w];

        //--------------------------------------------------------------------------------------
            for( int y = 0; y < h; y++ ) { for( int x = 0; x < w; x++ ) {
        //--------------------------------------------------------------------------------------

                edged[y][x] = pixels[y][x];

            //----------------------------------------------------------------------------------

                if( 0 == ( pixels[y][x] & 255 ) ) continue;

            //----------------------------------------------------------------------------------
                found : for( int i = -1; i <= 1; i++ ) { for( int j = -1; j <= 1; j++ ) {
            //----------------------------------------------------------------------------------

                    if( y + i < 0 || y + i >= h ) continue;
                    if( x + j < 0 || x + j >= w ) continue;

                //------------------------------------------------------------------------------

                    if( 0 == ( pixels[y + i][x + j] & 255 ) ) edged[y][x] = color;
                    if( 0 == ( pixels[y + i][x + j] & 255 ) ) break found;

        //--------------------------------------------------------------------------------------
            } } } }
        //--------------------------------------------------------------------------------------

            return edged;

        //--------------------------------------------------------------------------------------
        }

    //==========================================================================================
    // Save a lot of pixels
    //==========================================================================================

        static void saveModelImage( ItemStack stack , String name ) { try {
        //--------------------------------------------------------------------------------------

            int[][] data = get3DTexData( stack );

        //--------------------------------------------------------------------------------------

            int h = data.length;
            int w = data[0].length;

            BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        //--------------------------------------------------------------------------------------
            for( int y = 0; y < h; y++ ) { for( int x = 0; x < w; x++ ) {
        //--------------------------------------------------------------------------------------

                int R = ( data[y][x] >> 24 ) & 255;
                int G = ( data[y][x] >> 16 ) & 255;
                int B = ( data[y][x] >> 8  ) & 255;
                int A = ( data[y][x]       ) & 255;

                image.setRGB( x , y , ( A << 24 ) | ( R << 16 ) | ( G << 8 ) | ( B ) );

        //--------------------------------------------------------------------------------------
            } }
        //--------------------------------------------------------------------------------------

            Path path = Paths.get( Base.root + "/logs/" + name + ".png" );

        //--------------------------------------------------------------------------------------

            if( path.toFile().exists() ) FileUtils.deleteQuietly( path.toFile() );
            OutputStream output = Files.newOutputStream( path );

            ImageIO.write( image , "png" , output );

            output.flush();
            output.close();

        //--------------------------------------------------------------------------------------
        } catch ( IOException e ) { e.printStackTrace(); } }

        static void saveAllToFile() { try {
        //--------------------------------------------------------------------------------------

            int i = 0;
            int l = ItemBlocks.count;

        //--------------------------------------------------------------------------------------

            int w = w3D * (     ( l < 8 ? l     : 8 ) );
            int h = h3D * ( 1 + ( l > 8 ? l / 8 : 0 ) );

            BufferedImage image = new BufferedImage( w , h , BufferedImage.TYPE_INT_ARGB );

        //--------------------------------------------------------------------------------------
            for( ItemBlocks.Compressed cpr: ItemBlocks.entries ) { for( ItemX item: cpr.items ){
        //--------------------------------------------------------------------------------------

                int[][] data = get3DTexData( new ItemStack( item , 1 , 0 ) );

            //----------------------------------------------------------------------------------
                for( int y = 0; y < h3D; y++ ) { for( int x = 0; x < w3D; x++ ) {
            //----------------------------------------------------------------------------------

                    int R = ( data[y][x] >> 24 ) & 255;
                    int G = ( data[y][x] >> 16 ) & 255;
                    int B = ( data[y][x] >> 8  ) & 255;
                    int A = ( data[y][x]       ) & 255;

                    int color = ( A << 24 ) | ( R << 16 ) | ( G << 8 ) | ( B );

                    image.setRGB( w3D * ( i % 8 ) + x , h3D * ( i / 8 ) + y , color );

        //--------------------------------------------------------------------------------------
            } } i++; } }
        //--------------------------------------------------------------------------------------

            Path path = Paths.get( Base.root + "/logs/all.png" );

        //--------------------------------------------------------------------------------------

            if( path.toFile().exists() ) FileUtils.deleteQuietly( path.toFile() );
            OutputStream output = Files.newOutputStream( path );

            ImageIO.write( image , "png" , output );

            output.flush();
            output.close();

        //--------------------------------------------------------------------------------------
        } catch ( IOException e ) { e.printStackTrace(); } }


        public static void getNumbers() {/*
            String message = "1x4";
            int width = 100;
            int height = 100;
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

            Graphics2D graphics = img.createGraphics();
            graphics.setColor(Color.black);
            graphics.setFont(new Font("TimesRoman", Font.BOLD, 12));

            FontMetrics fontMetrics = graphics.getFontMetrics();
            int stringWidth = fontMetrics.stringWidth(message);
            int stringHeight = fontMetrics.getAscent();

            graphics.drawString(message, (width - stringWidth) / 2, height / 2 + stringHeight / 4);
            //*/
        }
    //==========================================================================================

    }

//==============================================================================================

