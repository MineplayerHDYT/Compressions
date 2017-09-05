//==================================================================================================

    package compressions;

//==================================================================================================

    import compressions.Blocks.Stem.TEData;

//==================================================================================================

    import mcp.MethodsReturnNonnullByDefault;
    import net.minecraft.client.Minecraft;
    import net.minecraft.client.renderer.*;
    import net.minecraft.client.renderer.block.model.BakedQuad;
    import net.minecraft.client.renderer.block.model.IBakedModel;
    import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
    import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
    import net.minecraft.client.renderer.texture.TextureManager;
    import net.minecraft.client.renderer.texture.TextureMap;
    import net.minecraft.client.renderer.texture.TextureUtil;
    import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
    import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
    import net.minecraft.client.shader.Framebuffer;
    import net.minecraft.item.Item;
    import net.minecraft.item.ItemStack;
    import net.minecraft.util.EnumFacing;
    import net.minecraft.util.math.BlockPos;
    import net.minecraftforge.client.ForgeHooksClient;
    import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
    import net.minecraftforge.common.MinecraftForge;
    import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
    import org.lwjgl.BufferUtils;
    import org.lwjgl.opengl.Display;
    import org.lwjgl.opengl.GL11;

//==================================================================================================

    import javax.annotation.ParametersAreNonnullByDefault;
    import java.awt.*;
    import java.nio.IntBuffer;
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

//==================================================================================================
    @MethodsReturnNonnullByDefault @ParametersAreNonnullByDefault
    @SuppressWarnings( { "WeakerAccess" , "CanBeFinal" } )
//==================================================================================================

    public class Renderers {

    //==============================================================================================
        public static Map<String , Integer> textures = new HashMap<>();
        public static Map<String , Color>    colors  = new HashMap<>();
    //==============================================================================================

        public static List<ItemStack> stacks = new ArrayList<>();

    //==============================================================================================

        @SubscribeEvent public static void getTextures( DrawScreenEvent.Post event ) {
        //------------------------------------------------------------------------------------------
        // Save all previous OpenGL state (So that we can restore it at the end)
        //------------------------------------------------------------------------------------------

            GL11.glPushAttrib( GL11.GL_ALL_ATTRIB_BITS );

            GL11.glMatrixMode( GL11.GL_MODELVIEW );
            GL11.glPushMatrix();

            GL11.glMatrixMode( GL11.GL_PROJECTION );
            GL11.glPushMatrix();

            GL11.glMatrixMode( GL11.GL_TEXTURE );
            GL11.glPushMatrix();

            GL11.glMatrixMode( GL11.GL_COLOR );
            GL11.glPushMatrix();

        //------------------------------------------------------------------------------------------
        // OpenGL setup (Like enabling stuff, setting up matrices)
        //------------------------------------------------------------------------------------------

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

        //- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -

            int w = 32;
            int h = 32;

            GL11.glMatrixMode( GL11.GL_PROJECTION );
            GL11.glLoadIdentity();

            GL11.glOrtho( -1.0 , 1.0 , -1.0 , 1.0 , -10.0 , 10.0 );
            GL11.glViewport( 0 , 0 , w , h );

        //- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -

            GL11.glMatrixMode( GL11.GL_MODELVIEW );
            GL11.glLoadIdentity();

        //------------------------------------------------------------------------------------------
        // Create and bind framebuffer (So that we're not drawing on the screen)
        //------------------------------------------------------------------------------------------

            Framebuffer frameBuffer = new Framebuffer( w , h , true );

            frameBuffer.setFramebufferColor( 0 , 0 , 0 , 0 );

        //------------------------------------------------------------------------------------------
            for( ItemStack stack : stacks ) {
        //------------------------------------------------------------------------------------------

                frameBuffer.framebufferClear();
                frameBuffer.bindFramebuffer( false );

            //--------------------------------------------------------------------------------------
            // Setup and draw the item/block
            //--------------------------------------------------------------------------------------

                GL11.glLoadIdentity();

                GL11.glScalef( 2.0f , 2.0f , 2.0f );

            //-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

                TextureManager texMan = Minecraft.getMinecraft().getTextureManager();
                texMan.bindTexture( TextureMap.LOCATION_BLOCKS_TEXTURE );


                IBakedModel model = Minecraft.getMinecraft()
                                             .getRenderItem()
                                             .getItemModelMesher()
                                             .getItemModel( stack );

                model = model.getOverrides().handleItemState( model , stack , null , null );
                model = ForgeHooksClient.handleCameraTransforms( model ,
                        ItemCameraTransforms.TransformType.GUI , false );

            //-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

                Minecraft minecraft = Minecraft.getMinecraft();
                RenderItem renderItem = minecraft.getRenderItem();
                renderItem.renderItem( stack , model );

            //--------------------------------------------------------------------------------------
            // Get and cache the texture pixels
            //--------------------------------------------------------------------------------------

                String ID = stack.getItem().getRegistryName().getResourceDomain()
                          + stack.getItem().getRegistryName().getResourcePath()
                          + stack.getMetadata()
                          + stack.getTagCompound();

            //-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

                IntBuffer buff = BufferUtils.createIntBuffer( w * h );

                GL11.glReadPixels( 0 , 0 , w , h , GL11.GL_RGBA , GL11.GL_UNSIGNED_BYTE , buff );

            //-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

                int[] pixels = new int[h * w];

                Integer R = 0;
                Integer G = 0;
                Integer B = 0;

                Integer count = 0;

            //-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
                for( int i = 0; i < h * w; i++ ) {
            //-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

                    pixels[i] = buff.get( i );

                //-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

                    if( 0 == ( ( pixels[i] >> 24 ) & 255 ) ) continue;

                    B += ( pixels[i] >> 16 ) & 255;
                    G += ( pixels[i] >> 8  ) & 255;
                    R += ( pixels[i] >> 0  ) & 255;

                    count++;

            //-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
                }
            //-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

                Renderers.colors.put( ID , new Color( (int) ( R * 1.0f / count )
                                                    , (int) ( G * 1.0f / count )
                                                    , (int) ( B * 1.0f / count ) ) );

            //-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
            //    int[][] aint    = new int[5 + 1][];
            //            aint[0] = pixels;
            //            aint    = TextureUtil.generateMipmapData( 5 , w , aint );
            //-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

                int texID = GL11.glGenTextures();

                GL11.glBindTexture( GL11.GL_TEXTURE_2D, texID );

            // -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -

                GL11.glTexEnvf( GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE );
                GL11.glTexParameterf( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR );
                GL11.glTexParameterf( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR );
                GL11.glTexParameterf( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT );
                GL11.glTexParameterf( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT );

            // -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -
                buff.rewind();
            // -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -

                GL11.glTexImage2D( GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, w, h, 0,
                        GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buff );

                GL11.glBindTexture( GL11.GL_TEXTURE_2D, 0 );

            //-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
//*/
                Renderers.textures.put( ID , texID );

            //-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    /*
                BufferedImage image = new BufferedImage( w , h , BufferedImage.TYPE_INT_ARGB );

                for( int i = 0; i < h * w; i++ ) // ABRG -> ARGB
                    image.setRGB( i % w , h - 1 - i / w , ( ( ( pixels[i] >> 24 ) & 255 ) << 24 ) |
                                                  ( ( ( pixels[i] >>  0 ) & 255 ) << 16 ) |
                                                  ( ( ( pixels[i] >>  8 ) & 255 ) <<  8 ) |
                                                  ( ( ( pixels[i] >> 16 ) & 255 ) <<  0 ) );

                Path path = Paths.get( Base.root + "/logs/Primjer.png" );

            //-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

                if( path.toFile().exists() ) FileUtils.deleteQuietly( path.toFile() );

                try {
                    OutputStream output = Files.newOutputStream( path );
                    ImageIO.write( image , "png" , output );

                    output.flush();
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }//*/


        //------------------------------------------------------------------------------------------
            }
        //------------------------------------------------------------------------------------------
        // Unbind and destroy the framebuffer (So that minecraft can draw normally)
        //------------------------------------------------------------------------------------------

            frameBuffer.unbindFramebuffer();
            frameBuffer.deleteFramebuffer();

        //------------------------------------------------------------------------------------------
        // Restore all previously saved OpenGL state (So that minecraft can draw normally)
        //------------------------------------------------------------------------------------------

            GL11.glMatrixMode( GL11.GL_COLOR );
            GL11.glPopMatrix();

            GL11.glMatrixMode( GL11.GL_TEXTURE );
            GL11.glPopMatrix();

            GL11.glMatrixMode( GL11.GL_PROJECTION );
            GL11.glPopMatrix();

            GL11.glMatrixMode( GL11.GL_MODELVIEW );
            GL11.glPopMatrix();

            GL11.glPopAttrib();

        //------------------------------------------------------------------------------------------
        // Unregister this listener (As we've gotten and cached the texture data)
        //------------------------------------------------------------------------------------------
            MinecraftForge.EVENT_BUS.unregister( Renderers.class ); stacks.clear();
        //------------------------------------------------------------------------------------------
        }

    //==============================================================================================

        public static void DrawQuad( int texID , float[] UV , int[] color ) {
        //------------------------------------------------------------------------------------------
        // Setup
        //------------------------------------------------------------------------------------------

            GL11.glEnable( GL11.GL_TEXTURE_2D );
            GL11.glBindTexture( GL11.GL_TEXTURE_2D , texID );

        //- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -

            float[][][] sides = new float[][][]
            /* Front Face  */ { { { 0.0f, 0.0f, 0.0f }   // Bottom Right
                                , { 0.0f, 1.0f, 0.0f }   // Top Right
                                , { 1.0f, 1.0f, 0.0f }   // Top Left
                                , { 1.0f, 0.0f, 0.0f } } // Bottom Left

            /* Back Face   */ , { { 1.0f, 0.0f, 1.0f }   // Bottom Right
                                , { 1.0f, 1.0f, 1.0f }   // Top Right
                                , { 0.0f, 1.0f, 1.0f }   // Top Left
                                , { 0.0f, 0.0f, 1.0f } } // Bottom Left

            /* Top Face    */ , { { 1.0f, 1.0f, 1.0f }   // Bottom Right
                                , { 1.0f, 1.0f, 0.0f }   // Top Right
                                , { 0.0f, 1.0f, 0.0f }   // Top Left
                                , { 0.0f, 1.0f, 1.0f } } // Bottom Left

            /* Bottom Face */ , { { 1.0f, 0.0f, 0.0f }   // Bottom Right
                                , { 1.0f, 0.0f, 1.0f }   // Top Right
                                , { 0.0f, 0.0f, 1.0f }   // Top Left
                                , { 0.0f, 0.0f, 0.0f } } // Bottom Left

            /* Right Face  */ , { { 0.0f, 0.0f, 1.0f }   // Bottom Right
                                , { 0.0f, 1.0f, 1.0f }   // Top Right
                                , { 0.0f, 1.0f, 0.0f }   // Top Left
                                , { 0.0f, 0.0f, 0.0f } } // Bottom Left

            /* Left Face   */ , { { 1.0f, 0.0f, 0.0f }   // Bottom Right
                                , { 1.0f, 1.0f, 0.0f }   // Top Right
                                , { 1.0f, 1.0f, 1.0f }   // Top Left
                                , { 1.0f, 0.0f, 1.0f } } /* Bottom Left */ };

            float[][] normals = { {  0.0f ,  0.0f , -1.0f }
                                , {  0.0f ,  0.0f , +1.0f }
                                , {  0.0f , +1.0f ,  0.0f }
                                , {  0.0f , -1.0f ,  0.0f }
                                , { -1.0f ,  0.0f ,  0.0f }
                                , { +1.0f ,  0.0f ,  0.0f }
            };

        //------------------------------------------------------------------------------------------
        // Draw
        //------------------------------------------------------------------------------------------

            GL11.glBegin( GL11.GL_QUADS );

        //- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -
            for( int i = 0; i < 6; i++ ) {
        //- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -

                GL11.glColor4f( color[0] * 1.0f / 255f
                              , color[1] * 1.0f / 255f
                              , color[2] * 1.0f / 255f
                              , color[3] * 1.0f / 255f );
                GL11.glNormal3f( normals[i][0] , normals[i][1] , normals[i][2] );
                GL11.glTexCoord2f( UV[1] , UV[2] );
                GL11.glVertex3f( sides[i][0][0], sides[i][0][1], sides[i][0][2] );

                GL11.glColor4f( color[0] * 1.0f / 255f
                              , color[1] * 1.0f / 255f
                              , color[2] * 1.0f / 255f
                              , color[3] * 1.0f / 255f );
                GL11.glNormal3f( normals[i][0] , normals[i][1] , normals[i][2] );
                GL11.glTexCoord2f( UV[1] , UV[3] );
                GL11.glVertex3f( sides[i][1][0], sides[i][1][1], sides[i][1][2] );

                GL11.glColor4f( color[0] * 1.0f / 255f
                              , color[1] * 1.0f / 255f
                              , color[2] * 1.0f / 255f
                              , color[3] * 1.0f / 255f );
                GL11.glNormal3f( normals[i][0] , normals[i][1] , normals[i][2] );
                GL11.glTexCoord2f( UV[0] , UV[3] );
                GL11.glVertex3f( sides[i][2][0], sides[i][2][1], sides[i][2][2] );

                GL11.glColor4f( color[0] * 1.0f / 255f
                              , color[1] * 1.0f / 255f
                              , color[2] * 1.0f / 255f
                              , color[3] * 1.0f / 255f );
                GL11.glNormal3f( normals[i][0] , normals[i][1] , normals[i][2] );
                GL11.glTexCoord2f( UV[0] , UV[2] );
                GL11.glVertex3f( sides[i][3][0], sides[i][3][1], sides[i][3][2] );

        //- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -
            }
        //- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -

            GL11.glEnd();

        //------------------------------------------------------------------------------------------
        }

    //==============================================================================================

        public static boolean Compressed( ItemStack stack , List<BakedQuad> quads ) {
        //------------------------------------------------------------------------------------------

            String ID = stack.getItem().getRegistryName().getResourceDomain()
                      + stack.getItem().getRegistryName().getResourcePath()
                      + stack.getMetadata()
                      + stack.getTagCompound();

        //------------------------------------------------------------------------------------------
        // We need an image of the item, but don't do that here
        //------------------------------------------------------------------------------------------

            if( !textures.containsKey( ID ) ) stacks.add( stack );
            //if( !textures.containsKey( ID ) ) getTextures( null );
            if( !textures.containsKey( ID ) ) MinecraftForge.EVENT_BUS.register(Renderers.class);
            if( !textures.containsKey( ID ) ) return false;

        //------------------------------------------------------------------------------------------

            GL11.glPushAttrib( GL11.GL_ALL_ATTRIB_BITS );

            GL11.glMatrixMode( GL11.GL_MODELVIEW );
            GL11.glPushMatrix();

            GL11.glMatrixMode( GL11.GL_PROJECTION );
            GL11.glPushMatrix();

            GL11.glMatrixMode( GL11.GL_TEXTURE );
            GL11.glPushMatrix();

            GL11.glMatrixMode( GL11.GL_COLOR );
            GL11.glPushMatrix();

        //------------------------------------------------------------------------------------------

            GL11.glShadeModel( GL11.GL_SMOOTH );

            GL11.glClearDepth( 1.0 );
            GL11.glEnable( GL11.GL_DEPTH_TEST );
            GL11.glDepthFunc( GL11.GL_LEQUAL );

            GL11.glEnable( GL11.GL_LIGHTING );

            GL11.glEnable( GL11.GL_COLOR_MATERIAL );
            GL11.glHint( GL11.GL_PERSPECTIVE_CORRECTION_HINT , GL11.GL_NICEST );

            GL11.glEnable( GL11.GL_ALPHA_TEST );
            GL11.glAlphaFunc( GL11.GL_GREATER , 0.1F );

            GL11.glEnable( GL11.GL_TEXTURE_2D );

            GL11.glDisable( GL11.GL_CULL_FACE );
            //GL11.glEnable( GL11.GL_CULL_FACE );
            //GL11.glCullFace( GL11.GL_BACK );

        //------------------------------------------------------------------------------------------

            Integer defID = Minecraft.getMinecraft()
                                     .getTextureManager()
                                     .getTexture( TextureMap.LOCATION_BLOCKS_TEXTURE )
                                     .getGlTextureId();

            DrawQuad( defID , new float[] { quads.get( 0 ).getSprite().getMinU()
                                          , quads.get( 0 ).getSprite().getMaxU()
                                          , quads.get( 0 ).getSprite().getMinV()
                                          , quads.get( 0 ).getSprite().getMaxV() }
                                          , new int[] { colors.get( ID ).getRed()
                                                      , colors.get( ID ).getGreen()
                                                      , colors.get( ID ).getBlue()
                                                      , 255 } );

        //- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -

            Integer texID = textures.get( ID );

            DrawQuad( texID , new float[] { 0 , 1 , 0 , 1 } , new int[] { 255 , 255 , 255 , 255 } );

        //------------------------------------------------------------------------------------------

            GL11.glMatrixMode( GL11.GL_COLOR );
            GL11.glPopMatrix();

            GL11.glMatrixMode( GL11.GL_TEXTURE );
            GL11.glPopMatrix();

            GL11.glMatrixMode( GL11.GL_PROJECTION );
            GL11.glPopMatrix();

            GL11.glMatrixMode( GL11.GL_MODELVIEW );
            GL11.glPopMatrix();

            GL11.glPopAttrib();

        //------------------------------------------------------------------------------------------
            return true;
        //------------------------------------------------------------------------------------------
        }

    //==============================================================================================

        public static class CmprTE extends TileEntitySpecialRenderer<TEData> {

        //==========================================================================================

            @Override public void render(
            //--------------------------------------------------------------------------------------
                    TEData te    ,
                    double x     ,
                    double y     ,
                    double z     ,
                    float  tick  ,
                    int    stage ,
                    float  alpha
            //--------------------------------------------------------------------------------------
            ) { if( !( te instanceof TEData ) ) return;
            //--------------------------------------------------------------------------------------
                //org.lwjgl.input.Mouse.setGrabbed(false);
            //--------------------------------------------------------------------------------------

                ItemStack stack = Blocks.Compressed.getPlaced( te.getPos() );
                stack = Items.Compressed.getRaw( stack );

                String ID = stack.getItem().getRegistryName().getResourceDomain()
                          + stack.getItem().getRegistryName().getResourcePath()
                          + stack.getMetadata()
                          + stack.getTagCompound();

/*
                GlStateManager.translate(x, y, z);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_BLEND);

                IBakedModel fallback = Models.Stem.fallback.get(
                            Models.Compressed.class.getName() );
                BakedQuad   square   = fallback.getQuads( null , EnumFacing.NORTH , 0 ).get(0);

                List<BakedQuad> quads     = new ArrayList<>();

                BakedQuad back = Models.Stem.Retexture( square ,  "back"  );
                          back = Models.Stem.PushQuad (  back  ,  -0.002f );

                quads.add( back );

                Compressed( stack , quads );//*/

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

                GL11.glEnable( GL11.GL_COLOR_MATERIAL );
                GL11.glHint( GL11.GL_PERSPECTIVE_CORRECTION_HINT , GL11.GL_NICEST );

                GL11.glEnable( GL11.GL_ALPHA_TEST );
                GL11.glAlphaFunc( GL11.GL_GREATER , 0.1F );

                GL11.glEnable( GL11.GL_TEXTURE_2D );

                GL11.glDisable( GL11.GL_CULL_FACE );
            //GL11.glEnable( GL11.GL_CULL_FACE );
            //GL11.glCullFace( GL11.GL_BACK );

            //--------------------------------------------------------------------------------------
                GlStateManager.disableLighting();
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 15 * 16, 15 * 16);

                //int bright = 0xF0;
                //int brightX = bright % 65536;
                //int brightY = bright / 65536;
                //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX,
                //        brightY);
                if (Minecraft.isAmbientOcclusionEnabled()) {
                    GlStateManager.shadeModel(GL11.GL_SMOOTH);
                } else {
                    GlStateManager.shadeModel(GL11.GL_FLAT);
                }

            //--------------------------------------------------------------------------------------
/*
                GL11.glDisable( GL11.GL_CULL_FACE );

                GlStateManager.enableRescaleNormal();
                GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
                GlStateManager.enableBlend();
                RenderHelper.enableStandardItemLighting();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA,
                        1, 0);//*/

            //--------------------------------------------------------------------------------------

                GL11.glMatrixMode( GL11.GL_MODELVIEW );

                GL11.glTranslated( x , y , z );

                IBakedModel fallback = Models.Stem.fallback.get(
                            Models.Compressed.class.getName() );
                BakedQuad   square   = fallback.getQuads( null , EnumFacing.NORTH , 0 ).get(0);

                BakedQuad back = Models.Stem.Retexture( square ,  "back"  );
                          back = Models.Stem.PushQuad (  back  ,  -0.002f );

                Integer defID = Minecraft.getMinecraft()
                                         .getTextureManager()
                                         .getTexture( TextureMap.LOCATION_BLOCKS_TEXTURE )
                                         .getGlTextureId();

                DrawQuad( defID , new float[] { back.getSprite().getMinU()
                                              , back.getSprite().getMaxU()
                                              , back.getSprite().getMinV()
                                              , back.getSprite().getMaxV() }
                                              , new int[] { colors.get( ID ).getRed()
                                                          , colors.get( ID ).getGreen()
                                                          , colors.get( ID ).getBlue()
                                                          , 255 } );

                Integer texID = textures.get( ID );
                DrawQuad( texID , new float[] {0, 1, 0, 1} , new int[] { 255 , 255 , 255 , 255 } );

            //--------------------------------------------------------------------------------------
            /*    GL11.glBegin( GL11.GL_QUADS );
            //--------------------------------------------------------------------------------------

                GL11.glColor4f( 1.0f , 1.0f , 1.0f , 1.0f );
                GL11.glNormal3f( 0.0f , 1.0f , 0.0f );
                GL11.glVertex3d( 0.0f, 1.0f, 0.0f );

                GL11.glColor4f( 1.0f , 1.0f , 1.0f , 1.0f );
                GL11.glNormal3f( 0.0f , 1.0f , 0.0f );
                GL11.glVertex3d( 0.0f, 1.0f, 1.0f );

                GL11.glColor4f( 1.0f , 1.0f , 1.0f , 1.0f );
                GL11.glNormal3f( 0.0f , 1.0f , 0.0f );
                GL11.glVertex3d( 1.0f, 1.0f, 1.0f );

                GL11.glColor4f( 1.0f , 1.0f , 1.0f , 1.0f );
                GL11.glNormal3f( 0.0f , 1.0f , 0.0f );
                GL11.glVertex3d( 1.0f, 1.0f, 0.0f );

            //--------------------------------------------------------------------------------------
                GL11.glEnd();//*/
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
            }

        //==========================================================================================

        }


    //==============================================================================================

        public static IntBuffer CaptureScreen() {
        //------------------------------------------------------------------------------------------
            //Minecraft.getMinecraft().updateDisplay();
        //------------------------------------------------------------------------------------------

            int w = Minecraft.getMinecraft().displayWidth;
            int h = Minecraft.getMinecraft().displayHeight;

            IntBuffer pixels = BufferUtils.createIntBuffer( w * h );

        //------------------------------------------------------------------------------------------

            int format = GL11.GL_RGBA;
            int type   = GL11.GL_UNSIGNED_BYTE;

            GL11.glReadPixels( 0 , 0 , w , h , format , type , pixels );

            int[] data = new int[w * h];
            for( int i = 0; i < w * h; i++ ) data[i] = pixels.get( i );

            pixels = BufferUtils.createIntBuffer( w * h );
            pixels.put( data );
            pixels.rewind();

            Minecraft.getMinecraft().updateDisplay();
        //------------------------------------------------------------------------------------------
            return pixels;
        //------------------------------------------------------------------------------------------
        }

        public static   void    DisplayScreen( IntBuffer pixels ) {
        //------------------------------------------------------------------------------------------

            int w = Minecraft.getMinecraft().displayWidth;
            int h = Minecraft.getMinecraft().displayHeight;

            int format = GL11.GL_RGBA;
            int type   = GL11.GL_UNSIGNED_BYTE;

            GL11.glDrawPixels( w , h , format , type , pixels );

        //------------------------------------------------------------------------------------------
            Minecraft.getMinecraft().updateDisplay();
        //------------------------------------------------------------------------------------------
        }

    //==============================================================================================

        public static int[][] GetCube( ItemStack stack ) {
        //------------------------------------------------------------------------------------------

            Display.update();

            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.disableColorLogic();
            GlStateManager.disableColorMaterial();
            GlStateManager.disableCull();
            GlStateManager.disableDepth();
            GlStateManager.disableFog();
            GlStateManager.disableLighting();
            GlStateManager.disableNormalize();
            GlStateManager.disableOutlineMode();
            GlStateManager.disablePolygonOffset();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableTexture2D();

            GlStateManager.clearColor( 0.5f , 0.5f, 0.5f , 0.5f );
            GlStateManager.clear( GL11.GL_COLOR_BUFFER_BIT );

            Display.update();
        //------------------------------------------------------------------------------------------

            Tessellator.getInstance().getBuffer().finishDrawing();
            Tessellator.getInstance().getBuffer().reset();

            Display.update();

            ItemStack gravel = new ItemStack(
                    Item.getItemFromBlock(net.minecraft.init.Blocks.GRAVEL) , 1 , 0);

            IBakedModel model2 = Minecraft.getMinecraft()
                    .getRenderItem()
                    .getItemModelMesher()
                    .getItemModel( gravel );

            model2 = model2.getOverrides().handleItemState( model2 , stack , null , null );
            model2 = ForgeHooksClient.handleCameraTransforms( model2, TransformType.GUI, false );

            Minecraft.getMinecraft().getRenderItem().renderItem( stack , model2 );

            Display.update();

            int w = 16;
            int h = 16;

        //------------------------------------------------------------------------------------------
            int[][] data = new int[EnumFacing.VALUES.length][h * w];
        //------------------------------------------------------------------------------------------
/*
            Framebuffer frameBuffer = new Framebuffer( w , h , true );

            frameBuffer.setFramebufferColor( 0 , 0 , 0 , 0 );
            frameBuffer.framebufferClear();

            frameBuffer.bindFramebuffer( true );//*/


        //------------------------------------------------------------------------------------------
            Minecraft.getMinecraft().updateDisplay();

            GL11.glPushAttrib( GL11.GL_ALL_ATTRIB_BITS );

            GL11.glMatrixMode( GL11.GL_MODELVIEW );
            GL11.glPushMatrix();

            GL11.glMatrixMode( GL11.GL_PROJECTION );
            GL11.glPushMatrix();

            GL11.glMatrixMode( GL11.GL_TEXTURE );
            GL11.glPushMatrix();

            GL11.glMatrixMode( GL11.GL_COLOR );
            GL11.glPushMatrix();

        //------------------------------------------------------------------------------------------

            GL11.glClearColor( 1.0f , 1.0f, 1.0f , 0.5f );
            GL11.glClear( GL11.GL_COLOR_BUFFER_BIT );
            GL11.glClear( GL11.GL_DEPTH_BUFFER_BIT );
            GL11.glClear( GL11.GL_STENCIL_BUFFER_BIT );

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

            GL11.glDisable( GL11.GL_CULL_FACE );
            GL11.glDisable( GL11.GL_TEXTURE_2D );

            Integer b = GL11.glGetInteger( GL11.GL_ALPHA_BITS );

        //------------------------------------------------------------------------------------------

            GL11.glMatrixMode( GL11.GL_PROJECTION );
            GL11.glLoadIdentity();

            GL11.glOrtho( -10.0 , 10.0 , -10.0 , 10.0 , -10.0 , 10.0 );
            //GL11.glViewport( 0 , 0 , w , h );
            GL11.glViewport( 0 , 0 , Minecraft.getMinecraft().displayWidth ,
                                     Minecraft.getMinecraft().displayHeight);

            GL11.glMatrixMode( GL11.GL_MODELVIEW );
            GL11.glLoadIdentity();
            //GL11.glTranslatef( -5 , -5 , -5 );


            //--------------------------------------------------------------------------------------
            GL11.glBegin( GL11.GL_QUADS );
            //--------------------------------------------------------------------------------------

            GL11.glVertex3f( 0.0f , 0.0f , 0.0f );
            GL11.glColor3f ( 1.0f , 1.0f , 1.0f );
            GL11.glTexCoord2f( 0.0f , 0.0f );

            GL11.glVertex3f( 0.0f , 1.0f , 0.0f );
            GL11.glColor3f ( 1.0f , 1.0f , 1.0f );
            GL11.glTexCoord2f( 0.0f , 1.0f );

            GL11.glVertex3f( 1.0f , 1.0f , 0.0f );
            GL11.glColor3f ( 1.0f , 1.0f , 1.0f );
            GL11.glTexCoord2f( 1.0f , 1.0f );

            GL11.glVertex3f( 1.0f , 0.0f , 0.0f );
            GL11.glColor3f ( 1.0f , 1.0f , 1.0f );
            GL11.glTexCoord2f( 1.0f , 0.0f );

            //--------------------------------------------------------------------------------------
            GL11.glEnd();

            Display.update();

        //------------------------------------------------------------------------------------------
            for( EnumFacing side : EnumFacing.VALUES ) {
        //------------------------------------------------------------------------------------------

                GL11.glLoadIdentity();

            //--------------------------------------------------------------------------------------
/*
                if( EnumFacing.DOWN  == side ) GL11.glRotatef( +90 , 1.0f , 0.0f , 0.0f );
                if( EnumFacing.UP    == side ) GL11.glRotatef( -90 , 1.0f , 0.0f , 0.0f );
                if( EnumFacing.NORTH == side ) GL11.glRotatef(   0 , 0.0f , 1.0f , 0.0f );
                if( EnumFacing.SOUTH == side ) GL11.glRotatef( 180 , 0.0f , 1.0f , 0.0f );
                if( EnumFacing.EAST  == side ) GL11.glRotatef( +90 , 0.0f , 1.0f , 0.0f );
                if( EnumFacing.WEST  == side ) GL11.glRotatef( -90 , 0.0f , 1.0f , 0.0f );

                GL11.glScalef( 0.6F , 0.6F , 0.6F );
                GL11.glTranslatef( 0.5F , 0.5F , 1.0F );//*/

            //--------------------------------------------------------------------------------------

                TextureManager texMan = Minecraft.getMinecraft().getTextureManager();
                texMan.bindTexture( TextureMap.LOCATION_BLOCKS_TEXTURE );

                Minecraft minecraft = Minecraft.getMinecraft();
                RenderItem renderItem = minecraft.getRenderItem();

                IBakedModel model = Minecraft.getMinecraft()
                        .getRenderItem()
                        .getItemModelMesher()
                        .getItemModel( stack );

                model = model.getOverrides().handleItemState( model , stack , null , null );
                model = ForgeHooksClient.handleCameraTransforms( model, TransformType.GUI, false );

                renderItem.renderItem( stack , model );

            //--------------------------------------------------------------------------------------
                //Minecraft.getMinecraft().updateDisplay();
                Display.update();
            //--------------------------------------------------------------------------------------

                IntBuffer buff = BufferUtils.createIntBuffer( w * h );
                GL11.glReadPixels( 0 , 0 , w , h , GL11.GL_RGBA , GL11.GL_UNSIGNED_BYTE , buff );

                for( int i = 0; i < h * w; i++ ) data[side.getIndex()][i] = buff.get( i );

        //------------------------------------------------------------------------------------------
            }
        //------------------------------------------------------------------------------------------

            GL11.glMatrixMode( GL11.GL_COLOR );
            GL11.glPopMatrix();

            GL11.glMatrixMode( GL11.GL_TEXTURE );
            GL11.glPopMatrix();

            GL11.glMatrixMode( GL11.GL_PROJECTION );
            GL11.glPopMatrix();

            GL11.glMatrixMode( GL11.GL_MODELVIEW );
            GL11.glPopMatrix();

            GL11.glPopAttrib();

            Tessellator.getInstance().getBuffer().begin( 7 , DefaultVertexFormats.ITEM );

        //------------------------------------------------------------------------------------------
/*
            frameBuffer.unbindFramebufferTexture();
            frameBuffer.unbindFramebuffer();
            frameBuffer.deleteFramebuffer();//*/

        //------------------------------------------------------------------------------------------
            return data;
        //------------------------------------------------------------------------------------------
        }

    //==============================================================================================

        public static Boolean once = false;

        public static void Compressed2( ItemStack stack ) {
        //------------------------------------------------------------------------------------------
            //org.lwjgl.input.Mouse.setGrabbed( false );
        //------------------------------------------------------------------------------------------

            String invenClass = "net.minecraft.client.renderer.RenderItem";
            String worldClass = "net.minecraftforge.client.model.pipeline.ForgeBlockModelRenderer";

            String invenMethod = "renderModel";
            String worldMethod = "render";

        //------------------------------------------------------------------------------------------

            StackTraceElement[] trace = Thread.currentThread().getStackTrace();

        //------------------------------------------------------------------------------------------
            if( 4 > trace.length ) return;
        //------------------------------------------------------------------------------------------

            String className  = trace[3].getClassName();
            String methodName = trace[3].getMethodName();

            Boolean inventory = className.equals( invenClass ) && methodName.equals( invenMethod );
            Boolean world     = className.equals( worldClass ) && methodName.equals( worldMethod );

        //------------------------------------------------------------------------------------------
            if( inventory ) {
        //------------------------------------------------------------------------------------------

                String ID = stack.getItem().getRegistryName().getResourceDomain()
                          + stack.getItem().getRegistryName().getResourcePath()
                          + stack.getMetadata()
                          + stack.getTagCompound();

            //--------------------------------------------------------------------------------------
                if( !textures.containsKey( ID ) ) {
            //--------------------------------------------------------------------------------------


                    //IntBuffer screen = CaptureScreen();

                    int[][] data = GetCube( stack );

                    Map<EnumFacing , Integer> quads = new HashMap<>();

                //--------------------------------------------------------------------------------------
                    for( int i = 0; i < data.length; i++ ) {
                //--------------------------------------------------------------------------------------

                        int texID = GL11.glGenTextures();

                        quads.put( EnumFacing.VALUES[i] , texID );

                        int[][] aint = new int[4 + 1][];
                        aint[0] = data[i];

                        aint = TextureUtil.generateMipmapData( 4 , 16 , aint );
                        TextureUtil.uploadTexture( texID , data[i] , 16 , 16 );
                        TextureUtil.uploadTextureMipmap( aint , 16 , 16 , 0 , 0, false, false);

                //--------------------------------------------------------------------------------------
                    }
                //--------------------------------------------------------------------------------------

                    //DisplayScreen( screen );

            //--------------------------------------------------------------------------------------
                }
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

                //GL11.glBindTexture( GL11.GL_TEXTURE_2D ,
                //        Renderers.textures.get( ID ).get( EnumFacing.NORTH ) );

            //--------------------------------------------------------------------------------------
                GL11.glBegin( GL11.GL_QUADS );
            //--------------------------------------------------------------------------------------

                GL11.glVertex3f( 0.0f , 0.0f , 0.0f );
                GL11.glColor3f ( 1.0f , 1.0f , 1.0f );
                GL11.glTexCoord2f( 0.0f , 0.0f );

                GL11.glVertex3f( 0.0f , 1.0f , 0.0f );
                GL11.glColor3f ( 1.0f , 1.0f , 1.0f );
                GL11.glTexCoord2f( 0.0f , 1.0f );

                GL11.glVertex3f( 1.0f , 1.0f , 0.0f );
                GL11.glColor3f ( 1.0f , 1.0f , 1.0f );
                GL11.glTexCoord2f( 1.0f , 1.0f );

                GL11.glVertex3f( 1.0f , 0.0f , 0.0f );
                GL11.glColor3f ( 1.0f , 1.0f , 1.0f );
                GL11.glTexCoord2f( 1.0f , 0.0f );

            //--------------------------------------------------------------------------------------

                GL11.glVertex3f( 0.0f , 0.0f , 1.0f );
                GL11.glColor3f ( 1.0f , 1.0f , 1.0f );
                GL11.glTexCoord2f( 0.0f , 0.0f );

                GL11.glVertex3f( 0.0f , 1.0f , 1.0f );
                GL11.glColor3f ( 1.0f , 1.0f , 1.0f );
                GL11.glTexCoord2f( 0.0f , 1.0f );

                GL11.glVertex3f( 1.0f , 1.0f , 1.0f );
                GL11.glColor3f ( 1.0f , 1.0f , 1.0f );
                GL11.glTexCoord2f( 1.0f , 1.0f );

                GL11.glVertex3f( 1.0f , 0.0f , 1.0f );
                GL11.glColor3f ( 1.0f , 1.0f , 1.0f );
                GL11.glTexCoord2f( 1.0f , 0.0f );

            //--------------------------------------------------------------------------------------
                GL11.glEnd();
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

        //------------------------------------------------------------------------------------------
            }
        //------------------------------------------------------------------------------------------
        }

    //==============================================================================================

    }

//==================================================================================================
