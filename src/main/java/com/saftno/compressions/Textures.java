//==================================================================================

    package com.saftno.compressions;

//==================================================================================

    import net.minecraft.block.Block;
    import net.minecraft.client.Minecraft;
    import net.minecraft.client.renderer.RenderItem;
    import net.minecraft.client.renderer.block.model.IBakedModel;
    import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
    import net.minecraft.client.renderer.texture.TextureManager;
    import net.minecraft.client.renderer.texture.TextureMap;
    import net.minecraft.client.shader.Framebuffer;
    import net.minecraft.item.ItemStack;
    import net.minecraft.util.ResourceLocation;
    import net.minecraft.util.math.AxisAlignedBB;
    import net.minecraftforge.client.ForgeHooksClient;
    import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
    import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
    import net.minecraftforge.common.MinecraftForge;
    import net.minecraftforge.fml.common.Mod;
    import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
    import org.apache.commons.io.FileUtils;
    import org.lwjgl.BufferUtils;
    import org.lwjgl.opengl.Display;
    import org.lwjgl.opengl.GL11;

//==================================================================================

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
    import java.util.HashSet;
    import java.util.Set;

//==================================================================================
    @SuppressWarnings( { "WeakerAccess" , "unused" } )  @Mod.EventBusSubscriber
//==================================================================================

    class Textures {

    //==============================================================================
    // Setup
    //==============================================================================

        public static Boolean over = false;

    //==============================================================================

        public static Set<String> textures = new HashSet<>();

    //==============================================================================

        public static IntBuffer ForgeEndScreen;

    //==============================================================================
        @SubscribeEvent
    //==============================================================================

        public static void forgeEnd( InitGuiEvent event ) {
        //--------------------------------------------------------------------------
            if( null != ForgeEndScreen) return;
        //--------------------------------------------------------------------------

            int w = Minecraft.getMinecraft().displayWidth;
            int h = Minecraft.getMinecraft().displayHeight;

            ForgeEndScreen = BufferUtils.createIntBuffer( w * h );

        //--------------------------------------------------------------------------

            int format = GL11.GL_RGBA;
            int type   = GL11.GL_UNSIGNED_BYTE;

            GL11.glReadPixels( 0 , 0 , w , h , format , type , ForgeEndScreen );

        //--------------------------------------------------------------------------
        }

    //==========================================================================
        //@SubscribeEvent
    //==========================================================================

        public static void Register( DrawScreenEvent event ) {
        //----------------------------------------------------------------------
            if( !Resources.storage.isOpen() ) return;
            //if( null != Resources.mod ) if( !Resources.mod.isOpen()
            //        ) return;
        //----------------------------------------------------------------------

            Textures.Generation.Blocks();

        //----------------------------------------------------------------------

            over = true;

        //----------------------------------------------------------------------
            MinecraftForge.EVENT_BUS.unregister( Textures.class );
        //----------------------------------------------------------------------
        }
    //==============================================================================
    // Usage
    //==============================================================================

        static class Generation {

        //==================================================================================

            static Boolean rotate = true;


            static int[][] getFileDataB( int[] bytes ) {
            //------------------------------------------------------------------------------

                int h = 16;
                int w = 16;

                int[][] data = new int[h][w];

            //------------------------------------------------------------------------------
                for( int y = 0; y < h; y++ ) { for( int x = 0; x < w; x++ ) {
            //------------------------------------------------------------------------------

                    int R = bytes[y * 16 * 4 + x * 4 + 0] & 255;
                    int G = bytes[y * 16 * 4 + x * 4 + 1] & 255;
                    int B = bytes[y * 16 * 4 + x * 4 + 2] & 255;
                    int A = bytes[y * 16 * 4 + x * 4 + 3] & 255;

                    data[y][x] = ( R << 24 ) | ( G << 16 ) | ( B << 8 ) | ( A );

            //------------------------------------------------------------------------------
                } }
            //------------------------------------------------------------------------------

                return data;

            //------------------------------------------------------------------------------
            }

            //==================================================================================
        // Get a lot of pixels
        //==================================================================================

            static int[][] getFileData( FileSystem mod , String name ) { try {
            //------------------------------------------------------------------------------

                String texLoc = null != mod ? "" : Base.root + "/../src/main/resources/";

                texLoc += "utility/" + name + ".png";

            //------------------------------------------------------------------------------

                Path path = null != mod ? mod.getPath( texLoc ): Paths.get( texLoc );

                if( !Files.exists( path ) ) return new int[1][1];

            //------------------------------------------------------------------------------

                InputStream input = Files.newInputStream( path );

                BufferedImage image = ImageIO.read( input );

                input.close();

            //------------------------------------------------------------------------------

                int h = 16;
                int w = 16;

                int[][] data = new int[h][w];

            //------------------------------------------------------------------------------
                for( int y = 0; y < h; y++ ) { for( int x = 0; x < w; x++ ) {
            //------------------------------------------------------------------------------

                    int A = ( image.getRGB( x , y ) >> 24 ) & 255;
                    int R = ( image.getRGB( x , y ) >> 16 ) & 255;
                    int G = ( image.getRGB( x , y ) >> 8  ) & 255;
                    int B = ( image.getRGB( x , y )       ) & 255;

                    data[y][x] = ( R << 24 ) | ( G << 16 ) | ( B << 8 ) | ( A );

            //------------------------------------------------------------------------------
                } }
            //------------------------------------------------------------------------------

                return data;

            //------------------------------------------------------------------------------
            } catch ( IOException e ) { e.printStackTrace(); return new int[1][1]; } }

            static int[][] get2DTexData( ItemStack stack ) {
            //------------------------------------------------------------------------------

                IBakedModel model = Minecraft.getMinecraft()
                        .getRenderItem()
                        .getItemModelMesher()
                        .getItemModel( stack );

                model = model.getOverrides().handleItemState( model , stack , null , null );
                model = ForgeHooksClient.handleCameraTransforms( model ,
                        ItemCameraTransforms.TransformType.GUI , false );

            //------------------------------------------------------------------------------

                int w = 16;
                int h = 16;

                Framebuffer frameBuffer = new Framebuffer( w , h , true );

                frameBuffer.setFramebufferColor( 0 , 0 , 0 , 0 );
                frameBuffer.framebufferClear();

                frameBuffer.bindFramebuffer( true );

            //------------------------------------------------------------------------------

                GL11.glPushAttrib( GL11.GL_ALL_ATTRIB_BITS );

                GL11.glMatrixMode( GL11.GL_MODELVIEW );
                GL11.glPushMatrix();

                GL11.glMatrixMode( GL11.GL_PROJECTION );
                GL11.glPushMatrix();

                GL11.glMatrixMode( GL11.GL_TEXTURE );
                GL11.glPushMatrix();

                GL11.glMatrixMode( GL11.GL_COLOR );
                GL11.glPushMatrix();

            //------------------------------------------------------------------------------

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

            //------------------------------------------------------------------------------

                GL11.glMatrixMode( GL11.GL_PROJECTION );
                GL11.glLoadIdentity();

                GL11.glOrtho( -0.5 , 0.5 , -0.5 , 0.5 , -1.0 , 1.0 );
                GL11.glViewport( 0 , 0 , w , h );

                GL11.glMatrixMode( GL11.GL_MODELVIEW );
                GL11.glLoadIdentity();

            //------------------------------------------------------------------------------

                Block block = Block.getBlockFromItem( stack.getItem() );

            //------------------------------------------------------------------------------
                if( net.minecraft.init.Blocks.AIR != block && rotate ) { try {
            //------------------------------------------------------------------------------

                    AxisAlignedBB box = block.getDefaultState().getBoundingBox(null , null);

                    if( box.maxY < 0.5f ) GL11.glRotatef( 90.0F , 1.0F , 0.0F , 0.0F );

            //------------------------------------------------------------------------------
                } catch( NullPointerException ex ) { int s = 0; } }
            //------------------------------------------------------------------------------

                ResourceLocation loc = stack.getItem().getRegistryName();

            //------------------------------------------------------------------------------
                if( null != loc ){
            //------------------------------------------------------------------------------

                    boolean bed    = loc.getResourcePath().contains( "bed" );
                    boolean shield = loc.getResourcePath().contains( "shield" );

                    if( bed )    GL11.glRotatef( +90.0F , 0.0F , 1.0F , 0.0F );

                    if( shield ) GL11.glScalef( 0.6F , 0.6F , 0.6F );
                    if( shield ) GL11.glTranslatef( 0.5F , 0.5F , 1.0F );

            //------------------------------------------------------------------------------
                }
            //------------------------------------------------------------------------------

                TextureManager texMan = Minecraft.getMinecraft().getTextureManager();
                texMan.bindTexture( TextureMap.LOCATION_BLOCKS_TEXTURE );

                Minecraft minecraft = Minecraft.getMinecraft();
                RenderItem renderItem = minecraft.getRenderItem();
                renderItem.renderItem( stack , model );

                Display.update();

            //------------------------------------------------------------------------------

                IntBuffer buff = BufferUtils.createIntBuffer( w * h );
                GL11.glReadPixels( 0, 0, w, h, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buff );

            //------------------------------------------------------------------------------

                GL11.glMatrixMode( GL11.GL_COLOR );
                GL11.glPopMatrix();

                GL11.glMatrixMode( GL11.GL_TEXTURE );
                GL11.glPopMatrix();

                GL11.glMatrixMode( GL11.GL_PROJECTION );
                GL11.glPopMatrix();

                GL11.glMatrixMode( GL11.GL_MODELVIEW );
                GL11.glPopMatrix();

                GL11.glPopAttrib();

            //------------------------------------------------------------------------------

                int[][] data = new int[h][w];

            //------------------------------------------------------------------------------
                for( int y = 0; y < h; y++ ) { for( int x = 0; x < w; x++ ) {
            //------------------------------------------------------------------------------

                    int A = ( buff.get( y * w + x ) >> 24 ) & 255;
                    int B = ( buff.get( y * w + x ) >> 16 ) & 255;
                    int G = ( buff.get( y * w + x ) >> 8  ) & 255;
                    int R = ( buff.get( y * w + x )       ) & 255;

                    data[h - 1 - y][x] = ( R << 24 ) | ( G << 16 ) | ( B << 8 ) | ( A );

            //------------------------------------------------------------------------------
                } }
            //------------------------------------------------------------------------------

                frameBuffer.unbindFramebufferTexture();
                frameBuffer.unbindFramebuffer();
                frameBuffer.deleteFramebuffer();

            //----------------------------------------------------------------------
            // Capturing frames in the framebuffer causes black frames to show
            //----------------------------------------------------------------------

                w = Minecraft.getMinecraft().displayWidth;
                h = Minecraft.getMinecraft().displayHeight;

                int format = GL11.GL_RGBA;
                int type   = GL11.GL_UNSIGNED_BYTE;

                GL11.glDrawPixels( w , h , format , type , ForgeEndScreen );

                Minecraft.getMinecraft().updateDisplay();

            //------------------------------------------------------------------------------
            // Crude fix for rails
            //------------------------------------------------------------------------------

                Boolean empty = ( 0 == ( averagePixel( data ) & 255 ) );

                if( empty ) rotate = false;
                if( empty ) data = get2DTexData( stack );
                if( empty ) rotate = true;

            //------------------------------------------------------------------------------

                return data;

            //------------------------------------------------------------------------------
            }

            static int[][] get3DTexData( ItemStack stack ) {
            //------------------------------------------------------------------------------

                IBakedModel model = Minecraft.getMinecraft()
                        .getRenderItem()
                        .getItemModelMesher()
                        .getItemModel( stack );

                model = model.getOverrides().handleItemState( model , stack , null , null );

            //------------------------------------------------------------------------------

                int w = 128;
                int h = 128;

                Framebuffer frameBuffer = new Framebuffer( w , h , true );

                frameBuffer.setFramebufferColor( 0 , 0 , 0 , 0 );
                frameBuffer.framebufferClear();

                frameBuffer.bindFramebuffer( true );

            //------------------------------------------------------------------------------

                GL11.glPushAttrib( GL11.GL_ALL_ATTRIB_BITS );

                GL11.glMatrixMode( GL11.GL_MODELVIEW );
                GL11.glPushMatrix();

                GL11.glMatrixMode( GL11.GL_PROJECTION );
                GL11.glPushMatrix();

                GL11.glMatrixMode( GL11.GL_TEXTURE );
                GL11.glPushMatrix();

                GL11.glMatrixMode( GL11.GL_COLOR );
                GL11.glPushMatrix();

            //------------------------------------------------------------------------------

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

            //------------------------------------------------------------------------------

                GL11.glMatrixMode( GL11.GL_PROJECTION );
                GL11.glLoadIdentity();

                GL11.glFrustum( -1.0 , 1.0 , -1.0 , 1.0 , -1.0 , 1.0 );
                GL11.glViewport( 0 , 0 , w , h );

                GL11.glMatrixMode( GL11.GL_MODELVIEW );
                GL11.glLoadIdentity();

            //------------------------------------------------------------------------------

                FloatBuffer lightPos = BufferUtils.createFloatBuffer( 4 );
                lightPos.put( new float[] { -1f , 2f , -2f , 1f } );
                lightPos.flip();

                FloatBuffer LightDiffuse = BufferUtils.createFloatBuffer(4);
                LightDiffuse.put( new float[]{ 1f , 1f , 1f , 0f } );
                LightDiffuse.flip();

                GL11.glLight(  GL11.GL_LIGHT1 , GL11.GL_DIFFUSE , LightDiffuse );
                GL11.glLight(  GL11.GL_LIGHT1 , GL11.GL_POSITION , lightPos );
                GL11.glLightf( GL11.GL_LIGHT1 , GL11.GL_LINEAR_ATTENUATION , 0.9f );

            //------------------------------------------------------------------------------

                GL11.glRotatef( -25.0F , 1.0F , 0.0F , 0.0F );
                GL11.glRotatef( +45.0F , 0.0F , 1.0F , 0.0F );

                TextureManager texMan = Minecraft.getMinecraft().getTextureManager();
                texMan.bindTexture( TextureMap.LOCATION_BLOCKS_TEXTURE );

                Minecraft minecraft = Minecraft.getMinecraft();
                RenderItem renderItem = minecraft.getRenderItem();
                renderItem.renderItem( stack , model );

                Display.update();

            //------------------------------------------------------------------------------

                IntBuffer buff = BufferUtils.createIntBuffer( w * h );
                GL11.glReadPixels( 0, 0, w, h, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buff );

            //------------------------------------------------------------------------------

                GL11.glMatrixMode( GL11.GL_COLOR );
                GL11.glPopMatrix();

                GL11.glMatrixMode( GL11.GL_TEXTURE );
                GL11.glPopMatrix();

                GL11.glMatrixMode( GL11.GL_PROJECTION );
                GL11.glPopMatrix();

                GL11.glMatrixMode( GL11.GL_MODELVIEW );
                GL11.glPopMatrix();

                GL11.glPopAttrib();

            //------------------------------------------------------------------------------

                int[][] data = new int[h][w];

            //------------------------------------------------------------------------------
                for( int y = 0; y < h; y++ ) { for( int x = 0; x < w; x++ ) {
            //------------------------------------------------------------------------------

                    int A = ( buff.get( y * w + x ) >> 24 ) & 255;
                    int B = ( buff.get( y * w + x ) >> 16 ) & 255;
                    int G = ( buff.get( y * w + x ) >> 8  ) & 255;
                    int R = ( buff.get( y * w + x )       ) & 255;

                    data[h - 1 - y][x] = ( R << 24 ) | ( G << 16 ) | ( B << 8 ) | ( A );

            //------------------------------------------------------------------------------
                } }
            //------------------------------------------------------------------------------

                frameBuffer.unbindFramebufferTexture();
                frameBuffer.unbindFramebuffer();
                frameBuffer.deleteFramebuffer();

            //------------------------------------------------------------------------------

                return data;

            //------------------------------------------------------------------------------
            }

        //==================================================================================
        // Manipulate a single pixel
        //==================================================================================

            static int averagePixel(int[][] pixels ) {
            //------------------------------------------------------------------------------

                int h = 16;
                int w = 16;

            //------------------------------------------------------------------------------

                int R = 0;
                int G = 0;
                int B = 0;
                int A = 0;

                int count = 0;

            //------------------------------------------------------------------------------
                for( int y = 0; y < h; y++ ) { for( int x = 0; x < w; x++ ) {
            //------------------------------------------------------------------------------

                    int r = ( pixels[y][x] >> 24 ) & 255;
                    int g = ( pixels[y][x] >> 16 ) & 255;
                    int b = ( pixels[y][x] >> 8  ) & 255;
                    int a = ( pixels[y][x]       ) & 255;

                    //if( a == 0 ) continue;

                    R += r;
                    G += g;
                    B += b;
                    A += a;

                    count++;

            //------------------------------------------------------------------------------
                } }
            //------------------------------------------------------------------------------

                R = (int) (( R / count ) * 1.0);
                G = (int) (( G / count ) * 1.0);
                B = (int) (( B / count ) * 1.0);
                A = (int) (( A / count ) * 1.0);

                R = R > 255 ? 255 : R;
                G = G > 255 ? 255 : G;
                B = B > 255 ? 255 : B;
                A = A > 255 ? 255 : A;

                return ( R << 24 ) | ( G << 16 ) | ( B << 8 ) | ( A );

            //------------------------------------------------------------------------------
            }

            static int darkenPixel(int color ) {
            //------------------------------------------------------------------------------

                int darker = 0;

            //------------------------------------------------------------------------------

                darker += ( (int) ( ( ( color >> 24 ) & 255 ) * 0.7 ) ) << 24;
                darker += ( (int) ( ( ( color >> 16 ) & 255 ) * 0.7 ) ) << 16;
                darker += ( (int) ( ( ( color >> 8  ) & 255 ) * 0.7 ) ) << 8 ;
                darker += ( (int) ( ( ( color       ) & 255 ) * 1.0 ) )      ;

            //------------------------------------------------------------------------------

                return darker;

            //------------------------------------------------------------------------------
            }

            static int getPixelHue(int color ) {
            //------------------------------------------------------------------------------

                int R = ( color >> 24 ) & 255;
                int G = ( color >> 16 ) & 255;
                int B = ( color >> 8  ) & 255;

            //------------------------------------------------------------------------------

                float min = R;

                if( R <= G && R <= B ) min = R;
                if( G <= B && G <= R ) min = G;
                if( B <= R && B <= G ) min = B;

            //------------------------------------------------------------------------------

                float max = R;

                if( R >= G && R >= B ) max = R;
                if( G >= B && G >= R ) max = G;
                if( B >= R && B >= G ) max = B;

            //------------------------------------------------------------------------------

                if( max == min ) return 0;

            //------------------------------------------------------------------------------

                float hue = 0f;

                if ( max == R ) { hue = 0f + ( G - B ) / ( max - min ); }
                if ( max == G ) { hue = 2f + ( B - R ) / ( max - min ); }
                if ( max == B ) { hue = 4f + ( R - G ) / ( max - min ); }

            //------------------------------------------------------------------------------

                hue = hue * 60;
                if( hue < 0 ) hue = hue + 360;

            //------------------------------------------------------------------------------
                return Math.round( hue );
            //------------------------------------------------------------------------------
            }

        //==================================================================================
        // Manipulate a lot of pixels
        //==================================================================================

            static int[][] darkenPixels( int step , int[][] pixels ) {
            //------------------------------------------------------------------------------

                int h = 16;
                int w = 16;

            //------------------------------------------------------------------------------
                for( int y = 0; y < step && y < h; y++ ) { for( int x =1+y-1; x<w-y; x++ ){
            //------------------------------------------------------------------------------

                    int end = h - y - 1;

                    for(int i=0; i<step-y; i++) pixels[ y ][x]=darkenPixel(pixels[ y ][x]);
                    for(int i=0; i<step-y; i++) pixels[end][x]=darkenPixel(pixels[end][x]);

            //------------------------------------------------------------------------------
                } } for(int x=0; x < step && x < w; x++) { for(int y=1+x; y < h-x-1; y++){
            //------------------------------------------------------------------------------

                    int end = h - x - 1;

                    for(int i=0; i<step-x; i++) pixels[y][ x ]=darkenPixel(pixels[y][ x ]);
                    for(int i=0; i<step-x; i++) pixels[y][end]=darkenPixel(pixels[y][end]);

            //------------------------------------------------------------------------------
                } }
            //------------------------------------------------------------------------------

                return pixels;

            //------------------------------------------------------------------------------
            }

            static int[][] joinPixels( int[][] under , int[][] above ) {
            //------------------------------------------------------------------------------

                int h = 16;
                int w = 16;

                int[][] joined = new int[h][w];

            //------------------------------------------------------------------------------
                for( int y = 0; y < h; y++ ) { for( int x = 0; x < w; x++ ) {
            //------------------------------------------------------------------------------

                    int uR = ( under[y][x] >> 24 ) & 255;
                    int uG = ( under[y][x] >> 16 ) & 255;
                    int uB = ( under[y][x] >> 8  ) & 255;
                    int uA = ( under[y][x]       ) & 255;

                //--------------------------------------------------------------------------

                    int aR = ( above[y][x] >> 24 ) & 255;
                    int aG = ( above[y][x] >> 16 ) & 255;
                    int aB = ( above[y][x] >> 8  ) & 255;
                    int aA = ( above[y][x]       ) & 255;

                //--------------------------------------------------------------------------

                    final int A = 255 - ( ( 255 - aA ) * ( 255 - uA ) ) / 255;

                //--------------------------------------------------------------------------

                    if( 0 == A ) joined[y][x] = 0;
                    if( 0 == A ) continue;

                //--------------------------------------------------------------------------

                    int R = ( ( ( 255 * aR * aA ) + ( uR * uA * (255 - aA) ) ) / A ) / 255;
                    int G = ( ( ( 255 * aG * aA ) + ( uG * uA * (255 - aA) ) ) / A ) / 255;
                    int B = ( ( ( 255 * aB * aA ) + ( uB * uA * (255 - aA) ) ) / A ) / 255;

                    joined[y][x] = ( R << 24 ) | ( G << 16 ) | ( B << 8 ) | ( A );

            //------------------------------------------------------------------------------
                } }
            //------------------------------------------------------------------------------

                return joined;

            //------------------------------------------------------------------------------
            }

            static int[][] colorPixels( int color , int[][] pixels ) {
            //------------------------------------------------------------------------------

                int h = 16;
                int w = 16;

                int[][] joined = new int[h][w];

            //------------------------------------------------------------------------------

                float[] cHSB = new float[3];

                int cR = ( color >> 24 ) & 255;
                int cG = ( color >> 16 ) & 255;
                int cB = ( color >> 8  ) & 255;

                Color.RGBtoHSB( cR , cG , cB , cHSB );

            //------------------------------------------------------------------------------
                for( int y = 0; y < h; y++ ) { for( int x = 0; x < w; x++ ) {
            //------------------------------------------------------------------------------

                    float[] HSB = new float[3];

                    int R = ( pixels[y][x] >> 24 ) & 255;
                    int G = ( pixels[y][x] >> 16 ) & 255;
                    int B = ( pixels[y][x] >> 8  ) & 255;
                    int A = ( pixels[y][x]       ) & 255;

                    Color.RGBtoHSB( R , G , B , HSB );

                //--------------------------------------------------------------------------

                    float hue        = getPixelHue( color ) * 1.0f / 360;
                    float brightness = HSB[2] * HSB[2];

                    Color hued = new Color( Color.HSBtoRGB( hue , cHSB[1] , brightness ) );

                //--------------------------------------------------------------------------

                    R = hued.getRed();
                    G = hued.getGreen();
                    B = hued.getBlue();

                //--------------------------------------------------------------------------

                    joined[y][x] = ( R << 24 ) | ( G << 16 ) | ( B << 8 ) | ( A );

            //------------------------------------------------------------------------------
                } }
            //------------------------------------------------------------------------------

                return joined;

            //------------------------------------------------------------------------------
            }

        //==================================================================================
        // Save a lot of pixels
        //==================================================================================

            @SuppressWarnings( "unused" ) static void saveModelImage( ItemStack stack , String name ) { try {
            //------------------------------------------------------------------------------

                int[][] data = get3DTexData( stack );

            //------------------------------------------------------------------------------

                int h = data.length;
                int w = data[0].length;

                BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

            //------------------------------------------------------------------------------
                for( int y = 0; y < h; y++ ) { for( int x = 0; x < w; x++ ) {
            //------------------------------------------------------------------------------

                    int R = ( data[y][x] >> 24 ) & 255;
                    int G = ( data[y][x] >> 16 ) & 255;
                    int B = ( data[y][x] >> 8  ) & 255;
                    int A = ( data[y][x]       ) & 255;

                    image.setRGB( x , y , ( A << 24 ) | ( R << 16 ) | ( G << 8 ) | ( B ) );

            //------------------------------------------------------------------------------
                } }
            //------------------------------------------------------------------------------

                Path path = Paths.get( Base.root + "/logs/" + name + ".png" );

            //------------------------------------------------------------------------------

                if( path.toFile().exists() ) FileUtils.deleteQuietly( path.toFile() );
                OutputStream output = Files.newOutputStream( path );

                ImageIO.write( image , "png" , output );

                output.flush();
                output.close();

            //------------------------------------------------------------------------------
            } catch ( IOException e ) { e.printStackTrace(); } }

            @SuppressWarnings( "unused" ) static void saveAllToFile() { try {
            //------------------------------------------------------------------------------

                int l = Blocks.blocks.values.size();

                int w = 128 * (     ( l < 8 ? l     : 8 ) );
                int h = 128 * ( 1 + ( l > 8 ? l / 8 : 0 ) );

                BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

            //------------------------------------------------------------------------------
                for(int i = 0; i < Blocks.blocks.values.size(); i++ ) {
            //------------------------------------------------------------------------------

                    Block entry = Blocks.blocks.values.get( i );

                    if( !( entry instanceof Blocks.Compressed) ) continue;

                    Blocks.Compressed block = (Blocks.Compressed) entry;

                    int[][] data = get3DTexData( new ItemStack( block , 1 , 0 ) );

                    //--------------------------------------------------------------------------
                    for( int y = 0; y < 128; y++ ) { for( int x = 0; x < 128; x++ ) {
                        //--------------------------------------------------------------------------

                        int R = ( data[y][x] >> 24 ) & 255;
                        int G = ( data[y][x] >> 16 ) & 255;
                        int B = ( data[y][x] >> 8  ) & 255;
                        int A = ( data[y][x]       ) & 255;

                        int color = ( A << 24 ) | ( R << 16 ) | ( G << 8 ) | ( B );

                        image.setRGB( 128 * (i % 8) + x , 128 * (i / 8) + y , color );

                        //------------------------------------------------------------------------------
                    } } }
                //------------------------------------------------------------------------------

                Path path = Paths.get( Base.root + "/logs/all.png" );

                //------------------------------------------------------------------------------

                if( path.toFile().exists() ) FileUtils.deleteQuietly( path.toFile() );
                OutputStream output = Files.newOutputStream( path );

                ImageIO.write( image , "png" , output );

                output.flush();
                output.close();

                //------------------------------------------------------------------------------
            } catch ( IOException e ) { e.printStackTrace(); } }

        //==================================================================================

            static void saveToJAR( int[][] data , Path path ) { try {
            //------------------------------------------------------------------------------
                if( Files.exists( path ) ) return;
            //------------------------------------------------------------------------------

                int h = data.length;
                int w = data[0].length;

                BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

            //------------------------------------------------------------------------------
                for( int y = 0; y < h; y++ ) { for( int x = 0; x < w; x++ ) {
            //------------------------------------------------------------------------------

                    int R = ( data[y][x] >> 24 ) & 255;
                    int G = ( data[y][x] >> 16 ) & 255;
                    int B = ( data[y][x] >> 8  ) & 255;
                    int A = ( data[y][x]       ) & 255;

                    image.setRGB( x , y , ( A << 24 ) | ( R << 16 ) | ( G << 8 ) | ( B ) );

            //------------------------------------------------------------------------------
                } }
            //------------------------------------------------------------------------------

                if(   Files.exists( path )   ) Files.delete( path );
                if( null != path.getParent() ) Files.createDirectories( path.getParent() );

            //------------------------------------------------------------------------------

                OutputStream output = Files.newOutputStream( path );

                ImageIO.write( image , "png" , output );

                output.flush();
                output.close();

            //------------------------------------------------------------------------------
            } catch ( IOException e ) { e.printStackTrace(); } }

        //==========================================================================
        // Generate extra textures from existing blocks
        //==========================================================================

            static void Blocks() {
            //----------------------------------------------------------------------
                if( Blocks.blocks.values.isEmpty() ) return;
            //----------------------------------------------------------------------

                //FileSystem mod = Resources.mod;
                FileSystem tmp = Resources.storage;

            //----------------------------------------------------------------------


                int[] frameI = new int[]{

                        0x24, 0x19, 0x09, 0xff, 0x2a, 0x1f, 0x11, 0xff, 0x33, 0x25, 0x10, 0xff, 0x3e, 0x29, 0x16, 0xff, 0x19, 0x10, 0x07, 0xff, 0x23, 0x19, 0x0f, 0xff, 0x3e, 0x2e, 0x15, 0xff, 0x1c,

                        0x13, 0x09, 0xff, 0x1b, 0x11, 0x07, 0xff, 0x36, 0x23, 0x13, 0xff, 0x33, 0x26, 0x12, 0xff, 0x2e, 0x1e, 0x0d, 0xff, 0x0e, 0x0a, 0x04, 0xff, 0x18, 0x11, 0x09, 0xff, 0x11, 0x0b,

                        0x05, 0xff, 0x16, 0x0e, 0x07, 0xff, 0x27, 0x19, 0x0c, 0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,

                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,

                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x2a, 0x1a, 0x0e, 0xff, 0x1e, 0x14, 0x08, 0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,

                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,

                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x1c, 0x12, 0x09, 0xff, 0x2e, 0x23, 0x14, 0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,

                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,

                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x29, 0x1c, 0x0b, 0xff, 0x1b, 0x14, 0x08, 0xff, 0x00,

                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,

                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x19, 0x11, 0x09,

                        0xff, 0x1b, 0x11, 0x07, 0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,

                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,

                        0x00, 0x00, 0x00, 0x0f, 0x09, 0x04, 0xff, 0x14, 0x0e, 0x07, 0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,

                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,

                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x39, 0x26, 0x0f, 0xff, 0x14, 0x0c, 0x06, 0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,

                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,

                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x08, 0x06, 0x02, 0xff, 0x19, 0x11, 0x07, 0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,

                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,

                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x1c, 0x14, 0x09, 0xff, 0x12, 0x0b, 0x06, 0xff,

                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,

                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x26, 0x19,

                        0x0e, 0xff, 0x12, 0x0c, 0x07, 0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,

                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,

                        0x00, 0x00, 0x00, 0x00, 0x2a, 0x17, 0x0c, 0xff, 0x34, 0x23, 0x0f, 0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,

                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,

                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x29, 0x1e, 0x0f, 0xff, 0x31, 0x22, 0x11, 0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,

                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,

                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x11, 0x0c, 0x04, 0xff, 0x31, 0x21, 0x10, 0xff, 0x00, 0x00, 0x00, 0x00, 0x00,

                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,

                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x45, 0x2e, 0x19, 0xff, 0x25, 0x19, 0x0e,

                        0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,

                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x25,

                        0x18, 0x0b, 0xff, 0x27, 0x19, 0x0c, 0xff, 0x32, 0x1f, 0x0f, 0xff, 0x24, 0x1b, 0x0a, 0xff, 0x3e, 0x26, 0x16, 0xff, 0x1d, 0x12, 0x09, 0xff, 0x38, 0x23, 0x11, 0xff, 0x2c, 0x1e,

                        0x0f, 0xff, 0x11, 0x0c, 0x05, 0xff, 0x33, 0x23, 0x0f, 0xff, 0x26, 0x1a, 0x0c, 0xff, 0x09, 0x06, 0x04, 0xff, 0x2f, 0x21, 0x10, 0xff, 0x29, 0x1c, 0x0d, 0xff, 0x23, 0x19, 0x0d,

                        0xff, 0x25, 0x19, 0x0e, 0xff, 0x1b, 0x12, 0x0a, 0xff
                };

                int[][] frame = getFileDataB( frameI );

            //----------------------------------------------------------------------

                int[] sideI = new int[]{
                        0x9a, 0x9a, 0x9a, 0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x92,

                        0x92, 0x92, 0xff, 0x84, 0x84, 0x84, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a,

                        0x9a, 0xff, 0x84, 0x84, 0x84, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x92, 0x92, 0x92, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x92, 0x92, 0x92, 0xff, 0x92, 0x92, 0x92,

                        0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x92, 0x92, 0x92, 0xff, 0x84, 0x84, 0x84, 0xff,

                        0x92, 0x92, 0x92, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x84, 0x84, 0x84, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x84,

                        0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x92, 0x92, 0x92, 0xff, 0x92, 0x92, 0x92, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x92, 0x92,

                        0x92, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x84, 0x84, 0x84, 0xff, 0x55, 0x55, 0x55, 0xff, 0x55, 0x55, 0x55, 0xff, 0x55, 0x55, 0x55,

                        0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x5f, 0x5f, 0x5f, 0xff,

                        0x55, 0x55, 0x55, 0xff, 0x55, 0x55, 0x55, 0xff, 0x55, 0x55, 0x55, 0xff, 0x55, 0x55, 0x55, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x84,

                        0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x84, 0x84, 0x84, 0xff, 0x9a, 0x9a,

                        0x9a, 0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x92, 0x92, 0x92, 0xff, 0x92, 0x92, 0x92, 0xff, 0x92, 0x92, 0x92, 0xff, 0x9a, 0x9a, 0x9a,

                        0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x92, 0x92, 0x92, 0xff, 0x92, 0x92, 0x92, 0xff, 0x92, 0x92, 0x92, 0xff, 0x9a, 0x9a, 0x9a, 0xff,

                        0x84, 0x84, 0x84, 0xff, 0x92, 0x92, 0x92, 0xff, 0x92, 0x92, 0x92, 0xff, 0x92, 0x92, 0x92, 0xff, 0x92, 0x92, 0x92, 0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x84,

                        0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x92, 0x92, 0x92, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a,

                        0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a,

                        0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x55, 0x55, 0x55, 0xff, 0x55, 0x55, 0x55, 0xff,

                        0x55, 0x55, 0x55, 0xff, 0x55, 0x55, 0x55, 0xff, 0x55, 0x55, 0x55, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x55, 0x55, 0x55, 0xff, 0x55,

                        0x55, 0x55, 0xff, 0x55, 0x55, 0x55, 0xff, 0x55, 0x55, 0x55, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a,

                        0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x84, 0x84, 0x84, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x92, 0x92, 0x92,

                        0xff, 0x92, 0x92, 0x92, 0xff, 0x92, 0x92, 0x92, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff,

                        0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x92, 0x92, 0x92, 0xff, 0x92, 0x92, 0x92, 0xff, 0x84,

                        0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84,

                        0x84, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x92, 0x92, 0x92, 0xff, 0x92, 0x92, 0x92, 0xff, 0x92, 0x92, 0x92, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x84, 0x84, 0x84,

                        0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x92, 0x92, 0x92, 0xff, 0x92, 0x92, 0x92, 0xff, 0x92, 0x92, 0x92, 0xff, 0x92, 0x92, 0x92, 0xff,

                        0x92, 0x92, 0x92, 0xff, 0x84, 0x84, 0x84, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x55, 0x55, 0x55, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x55, 0x55, 0x55, 0xff, 0x55,

                        0x55, 0x55, 0xff, 0x55, 0x55, 0x55, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x55, 0x55,

                        0x55, 0xff, 0x55, 0x55, 0x55, 0xff, 0x55, 0x55, 0x55, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a,

                        0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x84, 0x84, 0x84, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x84, 0x84, 0x84, 0xff,

                        0x84, 0x84, 0x84, 0xff, 0x92, 0x92, 0x92, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x92, 0x92, 0x92, 0xff, 0x84, 0x84, 0x84, 0xff, 0x5f,

                        0x5f, 0x5f, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x84, 0x84, 0x84, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x92, 0x92,

                        0x92, 0xff, 0x92, 0x92, 0x92, 0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x92, 0x92, 0x92, 0xff, 0x9a, 0x9a, 0x9a,

                        0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x84, 0x84, 0x84, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x84, 0x84, 0x84, 0xff,

                        0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x84, 0x84, 0x84, 0xff, 0x92, 0x92, 0x92, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a, 0x9a, 0x9a, 0xff, 0x9a,

                        0x9a, 0x9a, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x64, 0x64, 0x64, 0xff, 0x55, 0x55, 0x55, 0xff, 0x55, 0x55, 0x55, 0xff, 0x64, 0x64,

                        0x64, 0xff, 0x55, 0x55, 0x55, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x5f, 0x5f, 0x5f, 0xff, 0x55, 0x55, 0x55, 0xff, 0x3d, 0x3d, 0x3d, 0xff, 0x55, 0x55, 0x55,

                        0xff, 0x55, 0x55, 0x55, 0xff, 0x5f, 0x5f, 0x5f, 0xff
                };

                int[][] side = getFileDataB( sideI );

            //----------------------------------------------------------------------

                //int[][] frame = getFileData( mod , "frame" );
                //int[][] side  = getFileData( mod , "side" );

            //----------------------------------------------------------------------

                String texLoc = "/assets/" + Base.modId + "/textures/blocks/";

            //----------------------------------------------------------------------

                int avgPixel = 0;

                int[][] back   = null;
                int[][] pixels = null;
                int[][] backed = null;
                int[][] joined = null;

            //----------------------------------------------------------------------
                for( Block entry : Blocks.blocks ) {
            //----------------------------------------------------------------------

                    if( !( entry instanceof Blocks.Compressed ) ) continue;

                    //------------------------------------------------------------------

                    Blocks.Compressed block = (Blocks.Compressed) entry;

                    ItemStack stem = block.stem;

                    if( 1 == block.level ) pixels   = get2DTexData( stem );
                    if( 1 == block.level ) avgPixel = averagePixel( pixels );
                    if( 1 == block.level ) back     = colorPixels( avgPixel, side );
                    if( 1 == block.level ) backed   = frame;

                //------------------------------------------------------------------

                    ResourceLocation locBase = stem.getItem().getRegistryName();

                //------------------------------------------------------------------
                    if( null != locBase && 1 == block.level ) {
                //------------------------------------------------------------------

                        String name = locBase.getResourcePath();

                        Boolean transparent = false;

                        transparent = transparent ||  name.contains( "glass" );
                        transparent = transparent ||  name.contains( "ice" );
                        transparent = transparent && !name.contains( "bottle" );

                        if( !transparent ) backed = joinPixels( back , frame );

                //------------------------------------------------------------------
                    }
                //------------------------------------------------------------------

                    if( 1 == block.level ) joined = joinPixels( backed , pixels );

                //------------------------------------------------------------------

                    ResourceLocation loc = block.getRegistryName();

                //------------------------------------------------------------------
                    if( null == loc ) continue;
                //------------------------------------------------------------------

                    String name = loc.getResourcePath();
                    String file = texLoc + name + ".png";

                    int[][] meshed = darkenPixels( block.level , joined );

                //------------------------------------------------------------------

                    //if( null != mod ) saveToJAR( meshed , mod.getPath( file ) );
                    if( null != tmp ) saveToJAR( meshed , tmp.getPath( file ) );

                //------------------------------------------------------------------

                    textures.add( name );

        //--------------------------------------------------------------------------
            } }

        //==================================================================================

        }

    //======================================================================================

    }

//==========================================================================================

