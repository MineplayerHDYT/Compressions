//==========================================================================================

    package com.saftno.compressions;

//==========================================================================================

    import net.minecraft.client.Minecraft;
    import net.minecraft.client.renderer.RenderItem;
    import net.minecraft.client.renderer.block.model.IBakedModel;
    import net.minecraft.client.renderer.texture.TextureManager;
    import net.minecraft.client.renderer.texture.TextureMap;
    import net.minecraft.client.shader.Framebuffer;
    import net.minecraft.item.ItemStack;
    import net.minecraft.util.ResourceLocation;
    import org.apache.commons.io.FileUtils;
    import org.jetbrains.annotations.NotNull;
    import org.lwjgl.BufferUtils;
    import org.lwjgl.opengl.Display;
    import org.lwjgl.opengl.GL11;

//==========================================================================================

    import javax.imageio.ImageIO;
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
    import java.util.Arrays;

//==========================================================================================

    class Textures {

    //======================================================================================

        static class Generation {

        //==================================================================================
        // Get a lot of pixels
        //==================================================================================

            @NotNull static int[][] getFileData(FileSystem mod , String name ) { try {
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

            @NotNull static int[][] get2DTexData( ItemStack stack ) {
            //------------------------------------------------------------------------------

                IBakedModel model = Minecraft.getMinecraft()
                        .getRenderItem()
                        .getItemModelMesher()
                        .getItemModel( stack );

                model = model.getOverrides().handleItemState( model , stack , null , null );

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

            @NotNull static int[][] get3DTexData(ItemStack stack ) {
            //------------------------------------------------------------------------------

                IBakedModel model = Minecraft.getMinecraft()
                        .getRenderItem()
                        .getItemModelMesher()
                        .getItemModel( stack );

                model = model.getOverrides().handleItemState( model , stack , null , null );

            //------------------------------------------------------------------------------

                int w = 256;
                int h = 256;

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
                GL11.glLightf( GL11.GL_LIGHT1 , GL11.GL_LINEAR_ATTENUATION , 0.6f );

            //------------------------------------------------------------------------------

                GL11.glRotatef( -25.0F , 1.0F , 0.0F , 0.0F );
                GL11.glRotatef( -45.0F , 0.0F , 1.0F , 0.0F );

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

            static int averagePixel( int[][] pixels ) {
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

                    R += r;
                    G += g;
                    B += b;
                    A += a;

                    count++;

            //------------------------------------------------------------------------------
                } }
            //------------------------------------------------------------------------------

                R = R / count;
                G = G / count;
                B = B / count;
                A = 255;

                return ( R << 24 ) | ( G << 16 ) | ( B << 8 ) | ( A );

            //------------------------------------------------------------------------------
            }

            static int darkenPixel( int color ) {
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

        //==================================================================================
        // Manipulate a lot of pixels
        //==================================================================================

            @NotNull static int[][] darkenPixels( int step , int[][] pixels ) {
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

            @NotNull static int[][] joinPixels( int[][] under , int[][] above ) {
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

        //==================================================================================
        // Generate extra textures from existing blocks
        //==================================================================================

            static void Blocks() {
            //------------------------------------------------------------------------------
                if( null == Resources.tmp ) return;
            //------------------------------------------------------------------------------

                FileSystem mod = Resources.mod;
                FileSystem tmp = Resources.tmp;

            //------------------------------------------------------------------------------

                int[][] frame = getFileData( mod , "frame" );

                @SuppressWarnings("unused") int[][] mesh = getFileData( mod , "mesh"  );

            //------------------------------------------------------------------------------

                String texLoc = "/assets/" + Base.modId + "/textures/blocks/";

                int L1 = Blocks.Generation.blocks.length;
                int L2 = Configurations.getDepth() + 1;

            //------------------------------------------------------------------------------
                for( int y = 0; y < L1; y++ ) {
            //------------------------------------------------------------------------------

                    ItemStack base = Blocks.Generation.blocks[y][1].stem;

                    //saveModelImage( base , base.getDisplayName() );

                    int[][] pixels = get2DTexData( base );

                //--------------------------------------------------------------------------

                    int[][] back = new int[frame.length][frame[0].length];

                    for( int[] aBack : back ) Arrays.fill( aBack , averagePixel( pixels ) );

                //--------------------------------------------------------------------------

                    int[][] backed = joinPixels( back , frame );

                //--------------------------------------------------------------------------
                    for( int x = 1; x < L2; x++ ) {
                //--------------------------------------------------------------------------

                        Blocks.Compressed stack = Blocks.Generation.blocks[y][x];
                        ResourceLocation  loc   = stack.getRegistryName();

                        if( null == loc ) continue;

                    //----------------------------------------------------------------------

                        String name = loc.getResourcePath();
                        String file = texLoc + name + ".png";


                        int[][] meshed = darkenPixels( x , joinPixels( backed , pixels ) );

                    //----------------------------------------------------------------------

                        if( null != mod ) saveToJAR( meshed , mod.getPath( file ) );
                        if( null != tmp ) saveToJAR( meshed , tmp.getPath( file ) );

            //------------------------------------------------------------------------------
            } } }

        //==================================================================================

        }

    //======================================================================================

    }

//==========================================================================================

