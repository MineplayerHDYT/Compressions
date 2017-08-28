//==================================================================================================

    package compressions;

//==================================================================================================

    import mcp.MethodsReturnNonnullByDefault;
    import net.minecraft.block.state.IBlockState;
    import net.minecraft.client.Minecraft;
    import net.minecraft.client.renderer.block.model.*;
    import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
    import net.minecraft.client.renderer.color.ItemColors;
    import net.minecraft.client.renderer.texture.TextureAtlasSprite;
    import net.minecraft.client.renderer.vertex.VertexFormat;
    import net.minecraft.client.renderer.vertex.VertexFormatElement;
    import net.minecraft.client.resources.IResourceManager;
    import net.minecraft.entity.EntityLivingBase;
    import net.minecraft.item.*;
    import net.minecraft.nbt.NBTTagCompound;
    import net.minecraft.util.EnumFacing;
    import net.minecraft.util.ResourceLocation;
    import net.minecraft.util.math.BlockPos;
    import net.minecraft.world.World;
    import net.minecraftforge.client.event.ModelRegistryEvent;
    import net.minecraftforge.client.event.TextureStitchEvent;
    import net.minecraftforge.client.model.*;
    import net.minecraftforge.common.model.IModelState;
    import net.minecraftforge.common.property.IExtendedBlockState;
    import net.minecraftforge.common.property.IUnlistedProperty;
    import net.minecraftforge.fml.common.Mod;
    import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
    import org.apache.commons.lang3.tuple.ImmutablePair;
    import org.apache.commons.lang3.tuple.Pair;

//==================================================================================================

    import javax.annotation.Nullable;
    import javax.annotation.ParametersAreNonnullByDefault;
    import javax.vecmath.Matrix4f;
    import java.lang.reflect.Field;
    import java.util.*;
    import java.util.List;
    import java.util.function.Function;

//==================================================================================================
    @Mod.EventBusSubscriber @MethodsReturnNonnullByDefault @ParametersAreNonnullByDefault
    @SuppressWarnings( { "WeakerAccess" , "CanBeFinal" } )
//==================================================================================================

    public class Models {

    //==============================================================================================
        public static Map<String , Compressed> models = new HashMap<>();
    //==============================================================================================


        public static class Stem implements ICustomModelLoader , IModel , IBakedModel {

        //==========================================================================================
        // ICustomModelLoader
        //==========================================================================================

            public static Map<String , Boolean> finished = new HashMap<>();

        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

            @Override public boolean accepts( ResourceLocation modelLocation ) {
            //--------------------------------------------------------------------------------------
                if( finished.get( this.getClass().getName() ) ) return false;
            //--------------------------------------------------------------------------------------

                return modelLocation.getResourceDomain().equals( Base.modId );

            //--------------------------------------------------------------------------------------
            }

        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

            @Override public IModel loadModel(ResourceLocation modelLocation) throws Exception {
            //--------------------------------------------------------------------------------------

                return this;

            //--------------------------------------------------------------------------------------
            }

        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

            @Override public void onResourceManagerReload( IResourceManager resourceManager ) {
            //--------------------------------------------------------------------------------------

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
        // IModel
        //==========================================================================================

            public static Map<String , Items.Stem >   base   = new HashMap<>();
            public static Map<String , IBakedModel> fallback = new HashMap<>();

        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

            @Override public IBakedModel bake(
            //--------------------------------------------------------------------------------------
                    IModelState  state  ,
                    VertexFormat format ,
                    Function< ResourceLocation , TextureAtlasSprite > bakedTextureGetter
            //--------------------------------------------------------------------------------------
            ) { try { String ID = this.getClass().getName();
            //--------------------------------------------------------------------------------------

                if( fallback.containsKey( ID ) ) return this;

            //--------------------------------------------------------------------------------------
                finished.put( ID , true );
            //--------------------------------------------------------------------------------------

                ResourceLocation      RL    = base.get( ID ).getRegistryName();
                ModelResourceLocation MRL   = new ModelResourceLocation( RL , "inventory" );
                IModel                model = ModelLoaderRegistry.getModel( MRL );

            //--------------------------------------------------------------------------------------

                fallback.put( ID , model.bake( state , format , bakedTextureGetter ) );

            //--------------------------------------------------------------------------------------
                finished.put( ID , false );
            //--------------------------------------------------------------------------------------

                return this;

            //--------------------------------------------------------------------------------------
            } catch ( Exception ex ) { ex.printStackTrace(); return this; } }

        //==========================================================================================
        // IBakedModel
        //==========================================================================================

            public Map<EnumFacing , List<BakedQuad>> quads = new HashMap<>();

        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

            @Override public ItemOverrideList getOverrides() {
            //--------------------------------------------------------------------------------------
                return new ItemOverrideList( new ArrayList<>() );
            //--------------------------------------------------------------------------------------
            }

        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

            @Nullable public Stem getPlaced( @Nullable IBlockState state ) {
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
                if( null == stack ) return null;
            //--------------------------------------------------------------------------------------

                return (Stem) this.getOverrides().handleItemState( this , stack , null , null );

            //--------------------------------------------------------------------------------------
            }

        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

            @Override public List<BakedQuad> getQuads (
            //--------------------------------------------------------------------------------------
                    @Nullable IBlockState state ,
                    @Nullable EnumFacing  side  ,
                              long        rand
            //--------------------------------------------------------------------------------------
            ) { Map<EnumFacing , List<BakedQuad>> quads = this.quads;
            //--------------------------------------------------------------------------------------

                Stem placed = getPlaced( state );

                if( null != placed ) quads = placed.quads;

            //--------------------------------------------------------------------------------------
                IBakedModel fallback = Stem.fallback.get( this.getClass().getName() );
            //--------------------------------------------------------------------------------------

                if( !quads.containsKey( side ) ) return fallback.getQuads( state , side , rand );

            //--------------------------------------------------------------------------------------

                // Napraviti da se gleda da li je poziv iz RenderItem.renderModel
                //      Thread.currentThread().getStackTrace();
                // Ako je, vratiti prazni niz te prije vraćanja nacrtati predmet baš kao što bi
                // RenderItem.renderModel nacrtao

                return quads.get( side );

            //--------------------------------------------------------------------------------------
            }

            @Override public Pair<IBakedModel, Matrix4f> handlePerspective(TransformType type) {
            //--------------------------------------------------------------------------------------
                IBakedModel fallback = Stem.fallback.get( this.getClass().getName() );
            //--------------------------------------------------------------------------------------

                return new ImmutablePair<>( this , fallback.handlePerspective( type ).getRight() );

            //--------------------------------------------------------------------------------------
            }

        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

            @Override public boolean isAmbientOcclusion() {
            //--------------------------------------------------------------------------------------
                IBakedModel fallback = Stem.fallback.get( this.getClass().getName() );
            //--------------------------------------------------------------------------------------

                return fallback.isAmbientOcclusion();

            //--------------------------------------------------------------------------------------
            }

            @Override public boolean isGui3d() {
            //--------------------------------------------------------------------------------------
                IBakedModel fallback = Stem.fallback.get( this.getClass().getName() );
            //--------------------------------------------------------------------------------------

                return fallback.isGui3d();

            //--------------------------------------------------------------------------------------
            }

            @Override public boolean isBuiltInRenderer() {
            //--------------------------------------------------------------------------------------
                IBakedModel fallback = Stem.fallback.get( this.getClass().getName() );
            //--------------------------------------------------------------------------------------

                return fallback.isBuiltInRenderer();

            //--------------------------------------------------------------------------------------
            }

            @Override public TextureAtlasSprite getParticleTexture() {
            //--------------------------------------------------------------------------------------
                IBakedModel fallback = Stem.fallback.get( this.getClass().getName() );
            //--------------------------------------------------------------------------------------

                return fallback.getParticleTexture();

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
        // BakedQuad
        //==========================================================================================

            public static BakedQuad Colorize ( BakedQuad quad , Integer   color ) {
            //--------------------------------------------------------------------------------------
                if( -1 == color ) return quad;
            //--------------------------------------------------------------------------------------

                int[] data = new int[quad.getVertexData().length];

                for( int i = 0; i < data.length; i++ ) data[i] = quad.getVertexData()[i];

            //--------------------------------------------------------------------------------------

                BakedQuad newQuad = new BakedQuad( data
                                                 , quad.getTintIndex()
                                                 , quad.getFace()
                                                 , quad.getSprite()
                                                 , quad.shouldApplyDiffuseLighting()
                                                 , quad.getFormat() );

            //--------------------------------------------------------------------------------------
                for( int i = 0, s = 0; i < newQuad.getFormat().getElementCount(); i++ ) {
            //--------------------------------------------------------------------------------------

                    VertexFormatElement elem = newQuad.getFormat().getElement(i);

                //----------------------------------------------------------------------------------
                    if( elem.getUsage().equals( VertexFormatElement.EnumUsage.COLOR ) ) {
                //----------------------------------------------------------------------------------

                        int A1 = (newQuad.getVertexData()[s / 4        ] >> 24) & 255;
                        int A2 = (newQuad.getVertexData()[s / 4 + 7    ] >> 24) & 255;
                        int A3 = (newQuad.getVertexData()[s / 4 + 7 * 2] >> 24) & 255;
                        int A4 = (newQuad.getVertexData()[s / 4 + 7 * 3] >> 24) & 255;

                        int R = ( color >> 16 ) & 255;
                        int G = ( color >> 8  ) & 255;
                        int B = ( color >> 0  ) & 255;

                        int col = ( B << 16 ) + ( G << 8 ) + R;

                        newQuad.getVertexData()[s / 4        ] = col + ( A1 << 24 );
                        newQuad.getVertexData()[s / 4 + 7    ] = col + ( A2 << 24 );
                        newQuad.getVertexData()[s / 4 + 7 * 2] = col + ( A3 << 24 );
                        newQuad.getVertexData()[s / 4 + 7 * 3] = col + ( A4 << 24 );

                //----------------------------------------------------------------------------------
                    }
                //----------------------------------------------------------------------------------

                    s += elem.getSize();

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------

                return newQuad;

            //--------------------------------------------------------------------------------------
            }

            public static BakedQuad Retexture( BakedQuad quad , String    name  ) {
            //--------------------------------------------------------------------------------------

                String             position = "blocks/" + name;
                ResourceLocation   resLoc   = new ResourceLocation( Base.modId , position );
                TextureAtlasSprite sprite   = Minecraft.getMinecraft()
                                                       .getTextureMapBlocks()
                                                       .getTextureExtry( resLoc.toString() );

            //--------------------------------------------------------------------------------------

                Integer minU = Float.floatToRawIntBits( sprite.getMinU() );
                Integer minV = Float.floatToRawIntBits( sprite.getMinV() );
                Integer maxU = Float.floatToRawIntBits( sprite.getMaxU() );
                Integer maxV = Float.floatToRawIntBits( sprite.getMaxV() );

            //--------------------------------------------------------------------------------------

                int[] data = new int[quad.getVertexData().length];

                for( int i = 0; i < data.length; i++ ) data[i] = quad.getVertexData()[i];

            //--------------------------------------------------------------------------------------

                BakedQuad newQuad = new BakedQuad( data
                                                 , quad.getTintIndex()
                                                 , quad.getFace()
                                                 , sprite
                                                 , quad.shouldApplyDiffuseLighting()
                                                 , quad.getFormat() );

            //--------------------------------------------------------------------------------------
                for( int i = 0, s = 0; i < newQuad.getFormat().getElementCount(); i++ ) {
            //--------------------------------------------------------------------------------------

                    VertexFormatElement elem = newQuad.getFormat().getElement(i);

                //----------------------------------------------------------------------------------
                    if( elem.getUsage().equals( VertexFormatElement.EnumUsage.UV ) ) {
                //----------------------------------------------------------------------------------

                        newQuad.getVertexData()[s / 4 + 7 * 0 + 0] = minU;
                        newQuad.getVertexData()[s / 4 + 7 * 0 + 1] = minV;

                        newQuad.getVertexData()[s / 4 + 7 * 1 + 0] = minU;
                        newQuad.getVertexData()[s / 4 + 7 * 1 + 1] = maxV;

                        newQuad.getVertexData()[s / 4 + 7 * 2 + 0] = maxU;
                        newQuad.getVertexData()[s / 4 + 7 * 2 + 1] = maxV;

                        newQuad.getVertexData()[s / 4 + 7 * 3 + 0] = maxU;
                        newQuad.getVertexData()[s / 4 + 7 * 3 + 1] = minV;

                //----------------------------------------------------------------------------------
                    }
                //----------------------------------------------------------------------------------

                    s += elem.getSize();

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------

                return newQuad;

            //--------------------------------------------------------------------------------------
            }

            public static BakedQuad Retexture( BakedQuad quad , BakedQuad other ) {
            //--------------------------------------------------------------------------------------

                int[] data = new int[quad.getVertexData().length];

                for( int i = 0; i < data.length; i++ ) data[i] = quad.getVertexData()[i];

            //--------------------------------------------------------------------------------------

                BakedQuad newQuad = new BakedQuad( data
                                                 , quad.getTintIndex()
                                                 , quad.getFace()
                                                 , other.getSprite()
                                                 , quad.shouldApplyDiffuseLighting()
                                                 , quad.getFormat() );

                Integer minU = Float.floatToRawIntBits( other.getSprite().getMinU() );
                Integer minV = Float.floatToRawIntBits( other.getSprite().getMinV() );
                Integer maxU = Float.floatToRawIntBits( other.getSprite().getMaxU() );
                Integer maxV = Float.floatToRawIntBits( other.getSprite().getMaxV() );

            //--------------------------------------------------------------------------------------

                int[] in  = newQuad.getVertexData();
                int[] out = other.getVertexData();

                int[] signIn  = new int[3];
                int[] signOut = new int[3];

                Boolean hasSides = true;

            //--------------------------------------------------------------------------------------
                for( int i = 0, s = 0; i < newQuad.getFormat().getElementCount(); i++ ) {
            //--------------------------------------------------------------------------------------

                    VertexFormatElement elem = newQuad.getFormat().getElement(i);

                //----------------------------------------------------------------------------------
                    if( elem.getUsage().equals( VertexFormatElement.EnumUsage.NORMAL ) ) {
                //----------------------------------------------------------------------------------

                        signIn[0] = (int) Math.signum( (byte) ( ( in[s / 4] >> 0  ) & 255 ) );
                        signIn[1] = (int) Math.signum( (byte) ( ( in[s / 4] >> 8  ) & 255 ) );
                        signIn[2] = (int) Math.signum( (byte) ( ( in[s / 4] >> 16 ) & 255 ) );

                        signOut[0] = (int) Math.signum( (byte) ( ( out[s / 4] >> 0  ) & 255 ) );
                        signOut[1] = (int) Math.signum( (byte) ( ( out[s / 4] >> 8  ) & 255 ) );
                        signOut[2] = (int) Math.signum( (byte) ( ( out[s / 4] >> 16 ) & 255 ) );

                        hasSides = hasSides && ( signIn[0] == signOut[0] );
                        hasSides = hasSides && ( signIn[1] == signOut[1] );
                        hasSides = hasSides && ( signIn[2] == signOut[2] );

                //----------------------------------------------------------------------------------
                    }
                //----------------------------------------------------------------------------------

                    s += elem.getSize();

            //--------------------------------------------------------------------------------------
                } for( int i = 0, s = 0; i < newQuad.getFormat().getElementCount(); i++ ) {
            //--------------------------------------------------------------------------------------

                    VertexFormatElement elem = newQuad.getFormat().getElement(i);

                //----------------------------------------------------------------------------------
                    if( elem.getUsage().equals( VertexFormatElement.EnumUsage.POSITION ) ) {
                //----------------------------------------------------------------------------------
                        for( int y = 0; y < 4; y++ ) { for( int x = 0; x < 3; x++ ) {
                    //------------------------------------------------------------------------------

                            if(   !hasSides    ) continue;
                            if( 0 != signIn[x] ) continue;

                            in[s / 4 + 7 * y + x] = out[s / 4 + 7 * y + x];

                //----------------------------------------------------------------------------------
                    } } } if( elem.getUsage().equals( VertexFormatElement.EnumUsage.UV ) ) {
                //----------------------------------------------------------------------------------

                        newQuad.getVertexData()[s / 4            ] = minU;
                        newQuad.getVertexData()[s / 4         + 1] = minV;

                        newQuad.getVertexData()[s / 4 + 7        ] = minU;
                        newQuad.getVertexData()[s / 4 + 7     + 1] = maxV;

                        newQuad.getVertexData()[s / 4 + 7 * 2    ] = maxU;
                        newQuad.getVertexData()[s / 4 + 7 * 2 + 1] = maxV;

                        newQuad.getVertexData()[s / 4 + 7 * 3    ] = maxU;
                        newQuad.getVertexData()[s / 4 + 7 * 3 + 1] = minV;

                //----------------------------------------------------------------------------------
                    }
                //----------------------------------------------------------------------------------

                    s += elem.getSize();

            //--------------------------------------------------------------------------------------
                } return newQuad;
            //--------------------------------------------------------------------------------------
            }

            public static BakedQuad PushQuad ( BakedQuad quad , Float     push  ) {
            //--------------------------------------------------------------------------------------

                int[] data = new int[quad.getVertexData().length];

                for( int i = 0; i < data.length; i++ ) data[i] = quad.getVertexData()[i];

            //--------------------------------------------------------------------------------------

                BakedQuad newQuad = new BakedQuad( data
                                                 , quad.getTintIndex()
                                                 , quad.getFace()
                                                 , quad.getSprite()
                                                 , quad.shouldApplyDiffuseLighting()
                                                 , quad.getFormat() );

                int[] in   = newQuad.getVertexData();
                int[] sign = new int[3];

            //--------------------------------------------------------------------------------------
                for( int i = 0, s = 0; i < newQuad.getFormat().getElementCount(); i++ ) {
            //--------------------------------------------------------------------------------------

                    VertexFormatElement elem = newQuad.getFormat().getElement(i);

                //----------------------------------------------------------------------------------
                    if( elem.getUsage().equals( VertexFormatElement.EnumUsage.NORMAL ) ) {
                //----------------------------------------------------------------------------------

                        sign[0] = (int) Math.signum( (byte) ( ( in[s / 4] >> 0  ) & 255 ) );
                        sign[1] = (int) Math.signum( (byte) ( ( in[s / 4] >> 8  ) & 255 ) );
                        sign[2] = (int) Math.signum( (byte) ( ( in[s / 4] >> 16 ) & 255 ) );

                //----------------------------------------------------------------------------------
                    }
                //----------------------------------------------------------------------------------

                    s += elem.getSize();

            //--------------------------------------------------------------------------------------
                } for( int i = 0, s = 0; i < newQuad.getFormat().getElementCount(); i++ ) {
            //--------------------------------------------------------------------------------------

                    VertexFormatElement elem = newQuad.getFormat().getElement(i);

                //----------------------------------------------------------------------------------
                    if( elem.getUsage().equals( VertexFormatElement.EnumUsage.POSITION ) ) {
                //----------------------------------------------------------------------------------
                        for( int y = 0; y < 4; y++ ) { for( int x = 0; x < 3; x++ ) {
                    //------------------------------------------------------------------------------

                            Float value  = Float.intBitsToFloat( in[s / 4 + 7 * y + x] );

                            if( 0 != Math.signum( sign[x] ) ) value += push * Math.signum( sign[x]);
                            else if( value > 0.5f ) value += push;
                            else if( value < 0.5f ) value -= push;

                            in[s / 4 + 7 * y + x] = Float.floatToRawIntBits( value );

                //----------------------------------------------------------------------------------
                    } } }
                //----------------------------------------------------------------------------------

                    s += elem.getSize();

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------

                return newQuad;

            //--------------------------------------------------------------------------------------
            }

        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

            public static Integer   AvgColor ( BakedQuad quad , Integer   color ) {
            //--------------------------------------------------------------------------------------

                Integer base = quad.getVertexData()[quad.getFormat().getColorOffset() / 4];

                Integer bR = ( ( base       ) & 255 );
                Integer bG = ( ( base >> 8  ) & 255 );
                Integer bB = ( ( base >> 16 ) & 255 );

            //--------------------------------------------------------------------------------------

                Integer R = 0;
                Integer G = 0;
                Integer B = 0;

                Integer count = 0;

            //--------------------------------------------------------------------------------------
                for( Integer pixel : quad.getSprite().getFrameTextureData( 0 )[0] ) {
            //--------------------------------------------------------------------------------------

                    if( 0 == ( ( pixel >> 24 ) & 255 ) ) continue;

                    R += (int) ( ( ( ( pixel >> 16 ) & 255 ) * 1.0f / 255 ) * bR );
                    G += (int) ( ( ( ( pixel >> 8  ) & 255 ) * 1.0f / 255 ) * bG );
                    B += (int) ( ( ( ( pixel >> 0  ) & 255 ) * 1.0f / 255 ) * bB );

                    count++;

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------

                R = (int) ( R * 1.0f / count );
                B = (int) ( B * 1.0f / count );
                G = (int) ( G * 1.0f / count );

            //--------------------------------------------------------------------------------------

                Integer cR = ( color >> 16 ) & 255;
                Integer cG = ( color >> 8  ) & 255;
                Integer cB = ( color >> 0  ) & 255;

                if( -1 != color ) R = (int) ( ( R + cR ) * 0.5f );
                if( -1 != color ) B = (int) ( ( B + cB ) * 0.5f );
                if( -1 != color ) G = (int) ( ( G + cG ) * 0.5f );

            //--------------------------------------------------------------------------------------
                return ( R << 16 ) + ( G << 8 ) + B;
            //--------------------------------------------------------------------------------------
            }

        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

            public List<BakedQuad> getQuads( ItemStack stack, EnumFacing  side, long rand ) {
            //--------------------------------------------------------------------------------------
                List<BakedQuad> quads = new ArrayList<>();
            //--------------------------------------------------------------------------------------

                IBakedModel fallback = Stem.fallback.get( this.getClass().getName() );
                BakedQuad   square   = fallback.getQuads( null , side , rand ).get(0);

                ItemColors  colors   = Minecraft.getMinecraft().getItemColors();
                IBakedModel model    = Minecraft.getMinecraft()
                                                .getRenderItem()
                                                .getItemModelMesher()
                                                .getItemModel( stack )
                                                .handlePerspective( TransformType.GUI ).getLeft();

            //--------------------------------------------------------------------------------------

                List<BakedQuad>     old = model.getQuads( null , side , rand );
                if( old.isEmpty() ) old = model.getQuads( null , null , rand );

            //--------------------------------------------------------------------------------------
                for( BakedQuad quad : old ) {
            //--------------------------------------------------------------------------------------

                    Integer color = colors.getColorFromItemstack( stack , quad.getTintIndex() );

                //----------------------------------------------------------------------------------

                    quads.add( Colorize( Retexture( square , quad ) , color ) );

            //--------------------------------------------------------------------------------------
                } if( !quads.isEmpty() ) return quads;
            //--------------------------------------------------------------------------------------
            // Items with a built in renderer
            //--------------------------------------------------------------------------------------

                String name = stack.getUnlocalizedName();

            //--------------------------------------------------------------------------------------
            // Shield
            //--------------------------------------------------------------------------------------
                if( name.startsWith( "item.shield" ) ) {
            //--------------------------------------------------------------------------------------

                    square = Retexture( square , "shield" );

                    quads.add( square );

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------
            // Skull
            //--------------------------------------------------------------------------------------
                if( name.startsWith( "item.skull" ) ) {
            //--------------------------------------------------------------------------------------

                    if( name.contains( "wither"   ) ) square = Retexture( square , "head_wither"  );
                    if( name.contains( "skeleton" ) ) square = Retexture( square , "head_skeleton");
                    if( name.contains( "zombie"   ) ) square = Retexture( square , "head_zombie"  );
                    if( name.contains( "char"     ) ) square = Retexture( square , "head_person"  );
                    if( name.contains( "creeper"  ) ) square = Retexture( square , "head_creeper" );
                    if( name.contains( "dragon"   ) ) square = Retexture( square , "head_dragon"  );

                    quads.add( square );

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------
            // Chest and Trapped chest
            //--------------------------------------------------------------------------------------
                if( name.startsWith( "tile.chest" ) || name.startsWith( "tile.trapped_chest" ) ) {
            //--------------------------------------------------------------------------------------

                    if( side == EnumFacing.UP    ) square = Retexture( square , "chest_top"   );
                    if( side == EnumFacing.DOWN  ) square = Retexture( square , "chest_top"   );
                    if( side == EnumFacing.WEST  ) square = Retexture( square , "chest_side"  );
                    if( side == EnumFacing.SOUTH ) square = Retexture( square , "chest_side"  );
                    if( side == EnumFacing.EAST  ) square = Retexture( square , "chest_side"  );

                    if( side == EnumFacing.NORTH ) if( name.startsWith( "tile.chest" ) )
                        square = Retexture( square , "chest_front" );

                    if( side == EnumFacing.NORTH ) if( name.startsWith( "tile.trapped_chest" ) )
                        square = Retexture( square , "trapped_chest_front" );

                    quads.add( square );

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------
            // Shulker box
            //--------------------------------------------------------------------------------------
                if( name.startsWith( "tile.shulkerBox" ) ) {
                    for( EnumDyeColor color : EnumDyeColor.values() ) {
                        if( name.toLowerCase().contains( color.getUnlocalizedName().toLowerCase())){
            //--------------------------------------------------------------------------------------

                    if( side == EnumFacing.UP    ) square = Retexture( square , "shulker_top"  );
                    if( side == EnumFacing.DOWN  ) square = Retexture( square , "shulker_bot"  );
                    if( side == EnumFacing.NORTH ) square = Retexture( square , "shulker_side" );
                    if( side == EnumFacing.SOUTH ) square = Retexture( square , "shulker_side" );
                    if( side == EnumFacing.WEST  ) square = Retexture( square , "shulker_bot"  );
                    if( side == EnumFacing.EAST  ) square = Retexture( square , "shulker_bot"  );

                    square = Colorize( square , color.getColorValue() );

                    quads.add( square ); break;

            //--------------------------------------------------------------------------------------
                } } }
            //--------------------------------------------------------------------------------------
            // Bed
            //--------------------------------------------------------------------------------------
                if( name.startsWith( "item.bed" ) ) {
                    for( EnumDyeColor color : EnumDyeColor.values() ) {
                        if( name.contains( color.getUnlocalizedName() ) ) {
            //--------------------------------------------------------------------------------------

                    square = Retexture( square , "bed_frame"  );
                    quads.add( square );

                    square = Retexture( square , "bed_overlay"  );
                    square = Colorize( square , color.getColorValue() );
                    quads.add( square ); break;

            //--------------------------------------------------------------------------------------
                } } }
            //--------------------------------------------------------------------------------------
            // Banner
            //--------------------------------------------------------------------------------------
                if( name.startsWith( "tile.banner" ) ) {
                    for( EnumDyeColor color : EnumDyeColor.values() ) {
                        if( stack.getMetadata() == color.getDyeDamage() ) {
            //--------------------------------------------------------------------------------------

                    square = Retexture( square , "banner"  );
                    quads.add( square );

                    square = Retexture( square , "banner_layout"  );
                    square = Colorize( square , color.getColorValue() );
                    quads.add( square ); break;

            //--------------------------------------------------------------------------------------
                } } } return quads;
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
        // Setup
        //==========================================================================================

            Stem( Class value ) { try {
            //--------------------------------------------------------------------------------------
                String name = value.getName().split( "\\$" )[1].toLowerCase();
            //--------------------------------------------------------------------------------------

                Stem.finished.put( value.getName() , false );

            //--------------------------------------------------------------------------------------
                for( Field field : Items.class.getFields() ) {
            //--------------------------------------------------------------------------------------

                    if( !field.getName().toLowerCase().equals( name ) ) continue;

                    Stem.base.put( value.getName() , (Items.Stem) field.get( Items.class ) ); break;

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------
            } catch ( IllegalAccessException ex ) { ex.printStackTrace(); } }

        //==========================================================================================

        }


    //==============================================================================================


        public static class Compressed extends Stem {

        //==========================================================================================

            public static ItemOverrideList overrides = new ItemOverrideList(new ArrayList<>()){

            //======================================================================================

                @Override public IBakedModel handleItemState(IBakedModel m, ItemStack        s
                                                            ,World       w, EntityLivingBase e){
                //----------------------------------------------------------------------------------
                    if( !s.hasTagCompound() ) return m;
                //----------------------------------------------------------------------------------
                    NBTTagCompound tag = s.getTagCompound();
                //----------------------------------------------------------------------------------

                    String ID = "" + tag.getInteger( "Width" ) + tag.getInteger    ( "Height" )
                                   + tag.getString ( "Mod"   ) + tag.getString     ( "Entry"  )
                                   + tag.getInteger( "Meta"  ) + tag.getCompoundTag( "NBT"    );

                    ID = ID.toLowerCase();

                //----------------------------------------------------------------------------------
                    if( !models.containsKey( ID ) ) models.put( ID , new Compressed( s ) );
                //----------------------------------------------------------------------------------

                    return models.get( ID );

                //----------------------------------------------------------------------------------
                }

            //======================================================================================

            };

        //==========================================================================================
            @Override public ItemOverrideList getOverrides() { return overrides; }
        //==========================================================================================

            Compressed( ItemStack stack ) {
            //--------------------------------------------------------------------------------------
                super( Compressed.class ); if( null == stack ) return;
            //--------------------------------------------------------------------------------------

                NBTTagCompound compression = stack.getTagCompound();

            //--------------------------------------------------------------------------------------
                if( null == compression ) return; if( compression.hasNoTags() ) return;
            //--------------------------------------------------------------------------------------

                String         mod    = compression.getString     ( "Mod"   );
                String         entry  = compression.getString     ( "Entry" );
                Integer        meta   = compression.getInteger    ( "Meta"  );
                NBTTagCompound nbt    = compression.getCompoundTag( "NBT"   );

            //--------------------------------------------------------------------------------------
                List<ItemStack> in = new ArrayList<>( Configurations.entries );
            //--------------------------------------------------------------------------------------

                in.removeIf( s -> {
                    ResourceLocation loc = s.getItem().getRegistryName();
                    return null == loc || !loc.getResourceDomain().equals( mod );
                } );

                in.removeIf( s -> {
                    ResourceLocation loc = s.getItem().getRegistryName();
                    return null == loc || !loc.getResourcePath().equals( entry );
                } );

                in.removeIf( s -> !meta.equals( s.getMetadata() ) );

                in.removeIf( s -> {
                    NBTTagCompound tag = s.getTagCompound();
                    return null == tag || !tag.toString().replace( " ", "" ).toLowerCase().contains(
                            nbt.toString().replace( " ", "" ).toLowerCase() );
                } );

            //--------------------------------------------------------------------------------------
                if( in.isEmpty() ) return; stack = in.get( 0 );
            //--------------------------------------------------------------------------------------

                Integer height = compression.getInteger( "Height" );
                Integer rand   = new Random().nextInt();

            //--------------------------------------------------------------------------------------
                for( EnumFacing side : EnumFacing.values() ) {
            //--------------------------------------------------------------------------------------

                    List<BakedQuad> quads     = new ArrayList<>();
                    List<BakedQuad> itemQuads = getQuads( stack , side , rand );

                //----------------------------------------------------------------------------------

                    Integer avg = -1;
                    for( BakedQuad quad : itemQuads ) avg = AvgColor( quad , avg );

                //----------------------------------------------------------------------------------
                    IBakedModel fallback = Stem.fallback.get( this.getClass().getName() );
                    BakedQuad   square   = fallback.getQuads( null , side , rand ).get(0);
                //----------------------------------------------------------------------------------

                    BakedQuad back = Retexture( square ,  "back"  );
                              back = Colorize (  back  ,   avg    );
                              back = PushQuad (  back  ,  -0.002f );

                    quads.add( back );

                //----------------------------------------------------------------------------------

                    for( BakedQuad quad : itemQuads ) quads.add( PushQuad ( quad , -0.001f ) );

                //----------------------------------------------------------------------------------

                    BakedQuad front = Retexture( square ,  "frame" + height  );
                              front = Colorize (  front  ,   avg    );

                    quads.add( front );

                //----------------------------------------------------------------------------------

                    this.quads.put( side , quads );
                    this.quads.put( null , quads );

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
        }

    //==============================================================================================

        @SubscribeEvent public static void Register( ModelRegistryEvent event ) { try {
        //------------------------------------------------------------------------------------------
        //org.lwjgl.input.Mouse.setGrabbed( false );
        //------------------------------------------------------------------------------------------

            ModelLoaderRegistry.registerLoader( new Compressed( null ) );

        //------------------------------------------------------------------------------------------

            ResourceLocation itemRL  = Items.compressed.getRegistryName();
            ResourceLocation blockRL = Blocks.compressed.getRegistryName();

            ModelResourceLocation itemMRL  = new ModelResourceLocation( itemRL  , "inventory" );
            ModelResourceLocation blockMRL = new ModelResourceLocation( blockRL , "inventory" );

        //------------------------------------------------------------------------------------------

            ModelLoader.setCustomModelResourceLocation( Items.compressed, 0 , itemMRL  );
            ModelLoader.setCustomModelResourceLocation( Items.compressed, 0 , blockMRL );

        //------------------------------------------------------------------------------------------
        } catch( Exception ex ) { ex.printStackTrace(); } }

    //==============================================================================================

        @SubscribeEvent public static void onTextureStitch( TextureStitchEvent.Pre event ) {
        //------------------------------------------------------------------------------------------

            event.getMap().registerSprite( new ResourceLocation( Base.modId , "blocks/compressed"));
            event.getMap().registerSprite( new ResourceLocation( Base.modId , "blocks/back"   ) );
            event.getMap().registerSprite( new ResourceLocation( Base.modId , "blocks/frame0" ) );
            event.getMap().registerSprite( new ResourceLocation( Base.modId , "blocks/frame1" ) );
            event.getMap().registerSprite( new ResourceLocation( Base.modId , "blocks/frame2" ) );
            event.getMap().registerSprite( new ResourceLocation( Base.modId , "blocks/frame3" ) );
            event.getMap().registerSprite( new ResourceLocation( Base.modId , "blocks/frame4" ) );
            event.getMap().registerSprite( new ResourceLocation( Base.modId , "blocks/frame5" ) );
            event.getMap().registerSprite( new ResourceLocation( Base.modId , "blocks/frame6" ) );
            event.getMap().registerSprite( new ResourceLocation( Base.modId , "blocks/frame7" ) );
            event.getMap().registerSprite( new ResourceLocation( Base.modId , "blocks/frame8" ) );

            event.getMap().registerSprite( new ResourceLocation( Base.modId ,
                    "blocks/bed_frame" ) );
            event.getMap().registerSprite( new ResourceLocation( Base.modId ,
                    "blocks/bed_overlay" ) );
            event.getMap().registerSprite( new ResourceLocation( Base.modId ,
                    "blocks/banner" ) );
            event.getMap().registerSprite( new ResourceLocation( Base.modId ,
                    "blocks/banner_layout" ) );
            event.getMap().registerSprite( new ResourceLocation( Base.modId ,
                    "blocks/chest_front" ) );
            event.getMap().registerSprite( new ResourceLocation( Base.modId ,
                    "blocks/chest_side" ) );
            event.getMap().registerSprite( new ResourceLocation( Base.modId ,
                    "blocks/chest_top" ) );
            event.getMap().registerSprite( new ResourceLocation( Base.modId ,
                    "blocks/head_creeper" ) );
            event.getMap().registerSprite( new ResourceLocation( Base.modId ,
                    "blocks/head_person" ) );
            event.getMap().registerSprite( new ResourceLocation( Base.modId ,
                    "blocks/head_skeleton" ) );
            event.getMap().registerSprite( new ResourceLocation( Base.modId ,
                    "blocks/head_wither" ) );
            event.getMap().registerSprite( new ResourceLocation( Base.modId ,
                    "blocks/head_zombie" ) );
            event.getMap().registerSprite( new ResourceLocation( Base.modId ,
                    "blocks/head_dragon" ) );
            event.getMap().registerSprite( new ResourceLocation( Base.modId ,
                    "blocks/shield" ) );
            event.getMap().registerSprite( new ResourceLocation( Base.modId ,
                    "blocks/shulker_bot" ) );
            event.getMap().registerSprite( new ResourceLocation( Base.modId ,
                    "blocks/shulker_side" ) );
            event.getMap().registerSprite( new ResourceLocation( Base.modId ,
                    "blocks/shulker_top" ) );
            event.getMap().registerSprite( new ResourceLocation( Base.modId ,
                    "blocks/trapped_chest_front" ) );

        //------------------------------------------------------------------------------------------
        }

    //==============================================================================================









































    //==============================================================================================

/*
        public static class Compressed2 implements ICustomModelLoader , IModel , IBakedModel {

        //==========================================================================================
            public static IBakedModel base;
        //==========================================================================================

            public Map<EnumFacing , List<BakedQuad>> quads = new HashMap<>();

            public List<BakedQuad> allQuads = new ArrayList<>();

            public Map<String , Textures.Compressed> textures = new HashMap<>();

        //==========================================================================================
        // Textures
        //==========================================================================================


            public static BakedQuad TurnQ ( BakedQuad quad , EnumFacing side  ) {
            //--------------------------------------------------------------------------------------
                BakedQuad square = Compressed.base.getQuads( null , side , 0 ).get(0);
            //--------------------------------------------------------------------------------------

                int[] data = new int[quad.getVertexData().length];

                for( int i = 0; i < data.length; i++ ) data[i] = quad.getVertexData()[i];

            //--------------------------------------------------------------------------------------

                BakedQuad newQuad = new BakedQuad( data
                                                 , quad.getTintIndex()
                                                 , side
                                                 , quad.getSprite()
                                                 , quad.shouldApplyDiffuseLighting()
                                                 , quad.getFormat() );

                int[] in  = newQuad.getVertexData();
                int[] out = square.getVertexData();

            //--------------------------------------------------------------------------------------
                for( int i = 0, s = 0; i < newQuad.getFormat().getElementCount(); i++ ) {
            //--------------------------------------------------------------------------------------

                    VertexFormatElement elem = newQuad.getFormat().getElement(i);


                //----------------------------------------------------------------------------------
                    if( elem.getUsage().equals( VertexFormatElement.EnumUsage.POSITION ) ) {
                //----------------------------------------------------------------------------------


                    //------------------------------------------------------------------------------
                        for( int y = 0; y < 4; y++ ) { for( int x = 0; x < 2; x++ ) {
                    //------------------------------------------------------------------------------

                            in[s / 4 + 7 * y + x] = out[s / 4 + 7 * y + x];

                    //------------------------------------------------------------------------------
                        } }
                //----------------------------------------------------------------------------------
                    }
                //----------------------------------------------------------------------------------

                //----------------------------------------------------------------------------------
                    if( elem.getUsage().equals( VertexFormatElement.EnumUsage.NORMAL ) ) {
                //----------------------------------------------------------------------------------

                    //------------------------------------------------------------------------------
                        for( int y = 0; y < 4; y++ ) {
                    //------------------------------------------------------------------------------

                            in[s / 4 + 7 * y] = out[s / 4 + 7 * y];

                    //------------------------------------------------------------------------------
                        }
                //----------------------------------------------------------------------------------
                    }
                //----------------------------------------------------------------------------------

                    s += elem.getSize();

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------

                return newQuad;

            //--------------------------------------------------------------------------------------
            }

            public static BakedQuad LayerQ( BakedQuad quad , Integer    layer ) {
            //--------------------------------------------------------------------------------------

               return new BakedQuad( quad.getVertexData()
                        , layer
                        , quad.getFace()
                        , quad.getSprite()
                        , quad.shouldApplyDiffuseLighting()
                        , quad.getFormat() );

            //--------------------------------------------------------------------------------------
            }

            public static BakedQuad ColorQ( BakedQuad quad , Color      color ) {
            //--------------------------------------------------------------------------------------
                if( null == color ) return quad;
            //--------------------------------------------------------------------------------------
                if( -1 == color.getRGB() ) return quad;
            //--------------------------------------------------------------------------------------

                int[] data = new int[quad.getVertexData().length];

                for( int i = 0; i < data.length; i++ ) data[i] = quad.getVertexData()[i];

            //--------------------------------------------------------------------------------------

                BakedQuad newQuad = new BakedQuad( data
                                                 , quad.getTintIndex()
                                                 , quad.getFace()
                                                 , quad.getSprite()
                                                 , quad.shouldApplyDiffuseLighting()
                                                 , quad.getFormat() );

            //--------------------------------------------------------------------------------------
                for( int i = 0, s = 0; i < newQuad.getFormat().getElementCount(); i++ ) {
            //--------------------------------------------------------------------------------------

                    VertexFormatElement elem = newQuad.getFormat().getElement(i);

                //----------------------------------------------------------------------------------
                    if( elem.getUsage().equals( VertexFormatElement.EnumUsage.COLOR ) ) {
                //----------------------------------------------------------------------------------

                        int A1 = (newQuad.getVertexData()[s / 4 + 7 * 0] >> 24) & 255;
                        int A2 = (newQuad.getVertexData()[s / 4 + 7 * 1] >> 24) & 255;
                        int A3 = (newQuad.getVertexData()[s / 4 + 7 * 2] >> 24) & 255;
                        int A4 = (newQuad.getVertexData()[s / 4 + 7 * 3] >> 24) & 255;

                        int R = color.getRed();
                        int G = color.getGreen();
                        int B = color.getBlue();

                        int col = (B << 16) + (G << 8) + (R << 0);

                        newQuad.getVertexData()[s / 4 + 7 * 0] = ( col << 0 ) + ( A1 << 24 );
                        newQuad.getVertexData()[s / 4 + 7 * 1] = ( col << 0 ) + ( A2 << 24 );
                        newQuad.getVertexData()[s / 4 + 7 * 2] = ( col << 0 ) + ( A3 << 24 );
                        newQuad.getVertexData()[s / 4 + 7 * 3] = ( col << 0 ) + ( A4 << 24 );

                //----------------------------------------------------------------------------------
                    }
                //----------------------------------------------------------------------------------

                    s += elem.getSize();

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------

                return newQuad;

            //--------------------------------------------------------------------------------------
            }

            public static BakedQuad RetexQ( BakedQuad quad , String     name  ) {
            //--------------------------------------------------------------------------------------

                String             position = "blocks/" + name;
                ResourceLocation   resLoc   = new ResourceLocation( Base.modId , position );
                TextureAtlasSprite sprite   = Minecraft.getMinecraft()
                                                       .getTextureMapBlocks()
                                                       .getTextureExtry( resLoc.toString() );

            //--------------------------------------------------------------------------------------

                Integer minU = Float.floatToRawIntBits( sprite.getMinU() );
                Integer minV = Float.floatToRawIntBits( sprite.getMinV() );
                Integer maxU = Float.floatToRawIntBits( sprite.getMaxU() );
                Integer maxV = Float.floatToRawIntBits( sprite.getMaxV() );

            //--------------------------------------------------------------------------------------

                int[] data = new int[quad.getVertexData().length];

                for( int i = 0; i < data.length; i++ ) data[i] = quad.getVertexData()[i];

            //--------------------------------------------------------------------------------------

                BakedQuad newQuad = new BakedQuad( data
                                                 , quad.getTintIndex()
                                                 , quad.getFace()
                                                 , sprite
                                                 , quad.shouldApplyDiffuseLighting()
                                                 , quad.getFormat() );

            //--------------------------------------------------------------------------------------
                for( int i = 0, s = 0; i < newQuad.getFormat().getElementCount(); i++ ) {
            //--------------------------------------------------------------------------------------

                    VertexFormatElement elem = newQuad.getFormat().getElement(i);

                //----------------------------------------------------------------------------------
                    if( elem.getUsage().equals( VertexFormatElement.EnumUsage.UV ) ) {
                //----------------------------------------------------------------------------------

                        newQuad.getVertexData()[s / 4 + 7 * 0 + 0] = minU;
                        newQuad.getVertexData()[s / 4 + 7 * 0 + 1] = minV;

                        newQuad.getVertexData()[s / 4 + 7 * 1 + 0] = minU;
                        newQuad.getVertexData()[s / 4 + 7 * 1 + 1] = maxV;

                        newQuad.getVertexData()[s / 4 + 7 * 2 + 0] = maxU;
                        newQuad.getVertexData()[s / 4 + 7 * 2 + 1] = maxV;

                        newQuad.getVertexData()[s / 4 + 7 * 3 + 0] = maxU;
                        newQuad.getVertexData()[s / 4 + 7 * 3 + 1] = minV;

                //----------------------------------------------------------------------------------
                    }
                //----------------------------------------------------------------------------------

                    s += elem.getSize();

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------

                return newQuad;

            //--------------------------------------------------------------------------------------
            }

            public static BakedQuad RetexQ( BakedQuad quad , TextureAtlasSprite sprite  ) {

            //--------------------------------------------------------------------------------------

                Integer minU = Float.floatToRawIntBits( sprite.getMinU() );
                Integer minV = Float.floatToRawIntBits( sprite.getMinV() );
                Integer maxU = Float.floatToRawIntBits( sprite.getMaxU() );
                Integer maxV = Float.floatToRawIntBits( sprite.getMaxV() );

            //--------------------------------------------------------------------------------------

                int[] data = new int[quad.getVertexData().length];

                for( int i = 0; i < data.length; i++ ) data[i] = quad.getVertexData()[i];

            //--------------------------------------------------------------------------------------

                BakedQuad newQuad = new BakedQuad( data
                        , quad.getTintIndex()
                        , quad.getFace()
                        , sprite
                        , quad.shouldApplyDiffuseLighting()
                        , quad.getFormat() );

            //--------------------------------------------------------------------------------------
                for( int i = 0, s = 0; i < newQuad.getFormat().getElementCount(); i++ ) {
            //--------------------------------------------------------------------------------------

                    VertexFormatElement elem = newQuad.getFormat().getElement(i);

                //----------------------------------------------------------------------------------
                    if( elem.getUsage().equals( VertexFormatElement.EnumUsage.UV ) ) {
                //----------------------------------------------------------------------------------

                        newQuad.getVertexData()[s / 4 + 7 * 0 + 0] = minU;
                        newQuad.getVertexData()[s / 4 + 7 * 0 + 1] = minV;

                        newQuad.getVertexData()[s / 4 + 7 * 1 + 0] = minU;
                        newQuad.getVertexData()[s / 4 + 7 * 1 + 1] = maxV;

                        newQuad.getVertexData()[s / 4 + 7 * 2 + 0] = maxU;
                        newQuad.getVertexData()[s / 4 + 7 * 2 + 1] = maxV;

                        newQuad.getVertexData()[s / 4 + 7 * 3 + 0] = maxU;
                        newQuad.getVertexData()[s / 4 + 7 * 3 + 1] = minV;

                //----------------------------------------------------------------------------------
                    }
                //----------------------------------------------------------------------------------

                    s += elem.getSize();

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------

                return newQuad;

            //--------------------------------------------------------------------------------------
            }

            public static BakedQuad RetexQ( BakedQuad quad , BakedQuad other , Boolean item  ) {
            //--------------------------------------------------------------------------------------

                int[] data = new int[quad.getVertexData().length];

                for( int i = 0; i < data.length; i++ ) data[i] = quad.getVertexData()[i];

            //--------------------------------------------------------------------------------------

                BakedQuad newQuad = new BakedQuad( data
                        , quad.getTintIndex()
                        , quad.getFace()
                        , other.getSprite()
                        , quad.shouldApplyDiffuseLighting()
                        , quad.getFormat() );

                Integer minU = Float.floatToRawIntBits( other.getSprite().getMinU() );
                Integer minV = Float.floatToRawIntBits( other.getSprite().getMinV() );
                Integer maxU = Float.floatToRawIntBits( other.getSprite().getMaxU() );
                Integer maxV = Float.floatToRawIntBits( other.getSprite().getMaxV() );

                int[] in  = newQuad.getVertexData();
                int[] out = other.getVertexData();

                int[] sign = new int[3];

            //--------------------------------------------------------------------------------------
                for( int i = 0, s = 0; i < newQuad.getFormat().getElementCount(); i++ ) {
            //--------------------------------------------------------------------------------------

                    VertexFormatElement elem = newQuad.getFormat().getElement(i);

                //----------------------------------------------------------------------------------
                    if( elem.getUsage().equals( VertexFormatElement.EnumUsage.NORMAL ) ) {
                //----------------------------------------------------------------------------------

                        sign[0] = (int) Math.signum( (byte) ( ( in[s / 4] >> 0  ) & 255 ) );
                        sign[1] = (int) Math.signum( (byte) ( ( in[s / 4] >> 8  ) & 255 ) );
                        sign[2] = (int) Math.signum( (byte) ( ( in[s / 4] >> 16 ) & 255 ) );

                //----------------------------------------------------------------------------------
                    }
                //----------------------------------------------------------------------------------

                    s += elem.getSize();

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------


                //--------------------------------------------------------------------------------------
                for( int i = 0, s = 0; i < newQuad.getFormat().getElementCount(); i++ ) {
                //--------------------------------------------------------------------------------------

                    VertexFormatElement elem = newQuad.getFormat().getElement(i);

                //----------------------------------------------------------------------------------
                    if( elem.getUsage().equals( VertexFormatElement.EnumUsage.POSITION ) ) {
                //----------------------------------------------------------------------------------

                    //------------------------------------------------------------------------------
                        for( int y = 0; y < 4; y++ ) { for( int x = 0; x < 3; x++ ) {
                    //------------------------------------------------------------------------------

                            if( 0 != sign[x] ) continue;

                            if( !item ) in[s / 4 + 7 * y + x] = out[s / 4 + 7 * y + x];

                    //------------------------------------------------------------------------------
                        } }
                    //------------------------------------------------------------------------------


                //----------------------------------------------------------------------------------
                    }
                //----------------------------------------------------------------------------------

                //----------------------------------------------------------------------------------
                    if( elem.getUsage().equals( VertexFormatElement.EnumUsage.UV ) ) {
                //----------------------------------------------------------------------------------

                        newQuad.getVertexData()[s / 4 + 7 * 0 + 0] = minU;
                        newQuad.getVertexData()[s / 4 + 7 * 0 + 1] = minV;

                        newQuad.getVertexData()[s / 4 + 7 * 1 + 0] = minU;
                        newQuad.getVertexData()[s / 4 + 7 * 1 + 1] = maxV;

                        newQuad.getVertexData()[s / 4 + 7 * 2 + 0] = maxU;
                        newQuad.getVertexData()[s / 4 + 7 * 2 + 1] = maxV;

                        newQuad.getVertexData()[s / 4 + 7 * 3 + 0] = maxU;
                        newQuad.getVertexData()[s / 4 + 7 * 3 + 1] = minV;


                //----------------------------------------------------------------------------------
                    }
                //----------------------------------------------------------------------------------

                    s += elem.getSize();

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------

                return newQuad;

            //--------------------------------------------------------------------------------------
            }

            public static BakedQuad PushQ ( BakedQuad quad , Float     push   ) {
            //--------------------------------------------------------------------------------------

                int[] data = new int[quad.getVertexData().length];

                for( int i = 0; i < data.length; i++ ) data[i] = quad.getVertexData()[i];

            //--------------------------------------------------------------------------------------

                BakedQuad newQuad = new BakedQuad( data
                        , quad.getTintIndex()
                        , quad.getFace()
                        , quad.getSprite()
                        , quad.shouldApplyDiffuseLighting()
                        , quad.getFormat() );

                int[] in   = newQuad.getVertexData();
                int[] sign = new int[3];

            //--------------------------------------------------------------------------------------
                for( int i = 0, s = 0; i < newQuad.getFormat().getElementCount(); i++ ) {
            //--------------------------------------------------------------------------------------

                    VertexFormatElement elem = newQuad.getFormat().getElement(i);

                //----------------------------------------------------------------------------------
                    if( elem.getUsage().equals( VertexFormatElement.EnumUsage.NORMAL ) ) {
                //----------------------------------------------------------------------------------

                        sign[0] = (int) Math.signum( (byte) ( ( in[s / 4] >> 0  ) & 255 ) );
                        sign[1] = (int) Math.signum( (byte) ( ( in[s / 4] >> 8  ) & 255 ) );
                        sign[2] = (int) Math.signum( (byte) ( ( in[s / 4] >> 16 ) & 255 ) );

                //----------------------------------------------------------------------------------
                    }
                //----------------------------------------------------------------------------------

                    s += elem.getSize();

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------

            //--------------------------------------------------------------------------------------
                for( int i = 0, s = 0; i < newQuad.getFormat().getElementCount(); i++ ) {
            //--------------------------------------------------------------------------------------

                    VertexFormatElement elem = newQuad.getFormat().getElement(i);


                //----------------------------------------------------------------------------------
                    if( elem.getUsage().equals( VertexFormatElement.EnumUsage.POSITION ) ) {
                //----------------------------------------------------------------------------------

                    //------------------------------------------------------------------------------
                        for( int y = 0; y < 4; y++ ) { for( int x = 0; x < 3; x++ ) {
                    //------------------------------------------------------------------------------

                            Float value  = Float.intBitsToFloat( in[s / 4 + 7 * y + x] );
                                  value += push * Math.signum( sign[x] );

                            in[s / 4 + 7 * y + x] = Float.floatToRawIntBits( value );

                    //------------------------------------------------------------------------------
                        } }
                    //------------------------------------------------------------------------------

                //----------------------------------------------------------------------------------
                    }
                //----------------------------------------------------------------------------------

                    s += elem.getSize();

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------

                return newQuad;

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            public static Color AvgC( Integer   color , BakedQuad quad ) {
            //--------------------------------------------------------------------------------------

                Integer cR = ( color >> 16 ) & 255;
                Integer cG = ( color >> 8  ) & 255;
                Integer cB = ( color >> 0  ) & 255;
                Integer cA = 255;

            //--------------------------------------------------------------------------------------

                Integer R = 0;
                Integer G = 0;
                Integer B = 0;
                Integer A = 0;

                Integer count = 0;

            //--------------------------------------------------------------------------------------
                for( Integer pixel : quad.getSprite().getFrameTextureData( 0 )[0] ) {
            //--------------------------------------------------------------------------------------

                    if( 0 == ( pixel & 255 ) ) continue;

                    A += (int) ( ( ( ( pixel >> 24 ) & 255 ) * 1.0f / 255 ) * cA );
                    R += (int) ( ( ( ( pixel >> 16 ) & 255 ) * 1.0f / 255 ) * cR );
                    G += (int) ( ( ( ( pixel >> 8  ) & 255 ) * 1.0f / 255 ) * cG );
                    B += (int) ( ( ( ( pixel >> 0  ) & 255 ) * 1.0f / 255 ) * cB );

                    count++;

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------

                R = (int) ( R * 1.0f / count );
                B = (int) ( B * 1.0f / count );
                G = (int) ( G * 1.0f / count );
                A = (int) ( A * 1.0f / count );

                return new Color( R , G , B , A );

            //--------------------------------------------------------------------------------------
            }

            public static Color AvgC( ItemStack stack , List<BakedQuad> quads ) {
            //--------------------------------------------------------------------------------------
                ItemColors colors = Minecraft.getMinecraft().getItemColors();
            //--------------------------------------------------------------------------------------

                Color avg = null;

            //--------------------------------------------------------------------------------------
                for( BakedQuad quad : quads ) {
            //--------------------------------------------------------------------------------------

                    Integer base = colors.getColorFromItemstack( stack , quad.getTintIndex() );
                    Color   color = AvgC( base , quad );

                //----------------------------------------------------------------------------------
                    if( null == avg ) avg = color;
                //----------------------------------------------------------------------------------

                    avg = new Color( ( color.getRed()   + avg.getRed()   ) / 2
                                   , ( color.getGreen() + avg.getGreen() ) / 2
                                   , ( color.getBlue()  + avg.getBlue()  ) / 2
                                   , ( color.getAlpha() + avg.getAlpha() ) / 2 );

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------

                if( null != avg ) return avg;

            //--------------------------------------------------------------------------------------
                return new Color( 255 , 255 , 255 );
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
        // BakedQuad
        //==========================================================================================

            public Compressed( ItemStack base ) {
            //--------------------------------------------------------------------------------------
                if( null == base ) return;
            //--------------------------------------------------------------------------------------
                //org.lwjgl.input.Mouse.setGrabbed( false );
            //--------------------------------------------------------------------------------------
                if( !base.hasTagCompound() ) return;
            //--------------------------------------------------------------------------------------

                String         mod    = base.getTagCompound().getString     ( "Mod"    );
                String         entry  = base.getTagCompound().getString     ( "Entry"  );
                Integer        meta   = base.getTagCompound().getInteger    ( "Meta"   );
                NBTTagCompound nbt    = base.getTagCompound().getCompoundTag( "NBT"    );

                String ID = mod + entry + meta + nbt;

            //--------------------------------------------------------------------------------------
                List<ItemStack> in = new ArrayList<>( Configurations.entries );
            //--------------------------------------------------------------------------------------

                in.removeIf( s -> !s.getItem().getRegistryName().getResourceDomain().equals( mod ));
                in.removeIf( s -> !s.getItem().getRegistryName().getResourcePath().equals( entry ));
                in.removeIf( s -> !meta.equals( s.getMetadata() ) );
                in.removeIf( s -> !s.getTagCompound().toString().replace( " " , "" ).toLowerCase()
                                    .contains( nbt.toString().replace( " " , "" ) .toLowerCase() ));

            //--------------------------------------------------------------------------------------

                ItemStack stack  = in.get( 0 );
                Integer   height = base.getTagCompound().getInteger( "Height" );
                Integer   rand   = new Random().nextInt();

            //--------------------------------------------------------------------------------------
                for( EnumFacing side : EnumFacing.values() ) {
            //--------------------------------------------------------------------------------------

                    List<BakedQuad> quads  = new ArrayList<>();
                    BakedQuad       square = Compressed.base.getQuads( null , side , rand ).get(0);

                //----------------------------------------------------------------------------------

                    IBakedModel model = Minecraft.getMinecraft()
                                                 .getRenderItem()
                                                 .getItemModelMesher()
                                                 .getItemModel( stack )
                                                 .handlePerspective( TransformType.GUI ).getLeft();



                //----------------------------------------------------------------------------------

                    List<BakedQuad> oldQuads = new ArrayList<>();

                    oldQuads = model.getQuads( null , side , rand );

                    Boolean sideless = oldQuads.isEmpty();
                    if( oldQuads.isEmpty() ) oldQuads = model.getQuads( null , null , rand );
                    Boolean empty = oldQuads.isEmpty();



                    Color avgC = new Color( 255 , 255 , 255 );
                    if( empty ) {
                        oldQuads = new ArrayList<>();

                        String name = stack.getUnlocalizedName();

                        if( name.startsWith( "tile.trapped_chest" ) ) {
                            if( side == EnumFacing.UP || side == EnumFacing.DOWN ) {
                                BakedQuad layer0 = RetexQ( square , "chest_top"  );

                                oldQuads.add( layer0 );
                            }
                            else if( side == EnumFacing.NORTH ) {
                                BakedQuad layer0 = RetexQ( square , "trapped_chest_front"  );

                                oldQuads.add( layer0 );
                            }
                            else {
                                BakedQuad layer0 = RetexQ( square , "chest_side"  );

                                oldQuads.add( layer0 );
                            }

                            avgC = AvgC ( 16777215 , oldQuads.get( 0 ) );
                        }
                        if( name.startsWith( "tile.chest" ) ) {
                            if( side == EnumFacing.UP || side == EnumFacing.DOWN ) {
                                BakedQuad layer0 = RetexQ( square , "chest_top"  );

                                oldQuads.add( layer0 );
                            }
                            else if( side == EnumFacing.NORTH ) {
                                BakedQuad layer0 = RetexQ( square , "chest_front"  );

                                oldQuads.add( layer0 );
                            }
                            else {
                                BakedQuad layer0 = RetexQ( square , "chest_side"  );

                                oldQuads.add( layer0 );
                            }

                            avgC = AvgC ( 16777215 , oldQuads.get( 0 ) );
                        }

                        if( name.startsWith( "item.shield" ) ) {
                            oldQuads.add( RetexQ( square , "shield" ) );
                        }

                        if( name.startsWith( "item.skull" ) ) {
                            if( name.contains("wither") ) {
                                oldQuads.add( RetexQ( square , "head_wither" ) );
                            }
                            else if( name.contains("skeleton") ) {
                                oldQuads.add( RetexQ( square , "head_skeleton" ) );
                            }
                            else if( name.contains("zombie") ) {
                                oldQuads.add( RetexQ( square , "head_zombie" ) );
                            }
                            else if( name.contains("char") ) {
                                oldQuads.add( RetexQ( square , "head_person" ) );
                            }
                            else if( name.contains("creeper") ) {
                                oldQuads.add( RetexQ( square , "head_creeper" ) );
                            }
                            else if( name.contains("dragon") ) {
                                oldQuads.add( RetexQ( square , "head_dragon" ) );
                            }
                            avgC = AvgC ( 16777215 , oldQuads.get( 0 ) );
                        }


                        if( name.startsWith( "tile.shulkerBox" ) ) {
                            for( EnumDyeColor color : EnumDyeColor.values() ) {
                                if( name.toLowerCase().contains( color.getUnlocalizedName()
                                .toLowerCase() ) ) {
                                    Color col = new Color(
                                            ( color.getColorValue() >> 16) & 255,
                                            ( color.getColorValue() >> 8 ) & 255,
                                            ( color.getColorValue() >> 0 ) & 255
                                    );
                                    avgC = col;

                                    if( side == EnumFacing.UP ) {
                                        BakedQuad layer0 = RetexQ( square , "shulker_top"  );
                                                  layer0 = ColorQ( layer0   , col   );

                                        oldQuads.add( layer0 );
                                    }
                                    else if( side == EnumFacing.DOWN ) {
                                        BakedQuad layer0 = RetexQ( square , "shulker_bot"  );
                                                  layer0 = ColorQ( layer0   , col   );

                                        oldQuads.add( layer0 );
                                    }
                                    else {
                                        BakedQuad layer0 = RetexQ( square , "shulker_side"  );
                                                  layer0 = ColorQ( layer0   , col   );

                                        oldQuads.add( layer0 );
                                    } break;
                                }
                            }
                        }

                        if( name.startsWith( "item.bed." ) ) {
                            for( EnumDyeColor color : EnumDyeColor.values() ) {
                                if( name.contains( color.getUnlocalizedName() ) ) {
                                    Color col = new Color(
                                            ( color.getColorValue() >> 16) & 255,
                                            ( color.getColorValue() >> 8 ) & 255,
                                            ( color.getColorValue() >> 0 ) & 255
                                    );
                                    avgC = col;

                                    BakedQuad layer0 = RetexQ( square , "bed_frame"  );

                                    BakedQuad layer1 = RetexQ( square , "bed_overlay"  );
                                              layer1 = ColorQ( layer1   , col   );

                                    oldQuads.add( layer0 );
                                    oldQuads.add( layer1 );
                                }
                            }
                        }

                        if( name.startsWith( "tile.banner" ) ) {
                            for( EnumDyeColor color : EnumDyeColor.values() ) {
                                if( stack.getMetadata() == color.getDyeDamage() ) {
                                    Color col = new Color(
                                            ( color.getColorValue() >> 16) & 255,
                                            ( color.getColorValue() >> 8 ) & 255,
                                            ( color.getColorValue() >> 0 ) & 255
                                    );
                                    avgC = col;

                                    BakedQuad layer0 = RetexQ( square , "banner"  );

                                    BakedQuad layer1 = RetexQ( square , "banner_layout"  );
                                    layer1 = ColorQ( layer1   , col   );

                                    oldQuads.add( layer0 );
                                    oldQuads.add( layer1 );
                                }
                            }
                        }
                    }

                    //if( oldQuads.isEmpty() ) {
                    //    Textures.Compressed h;

                    //    if( !textures.containsKey( ID ) ) {
                    //        textures.put( ID , new Textures.Compressed( stack , side ) );
                    //    }

                    //    h = textures.get( ID );

                    //    oldQuads = new ArrayList<>();
                    //    oldQuads.add( Retexture( square , h ) );
                    //}

                //----------------------------------------------------------------------------------

                    int b = oldQuads.size() + 1;
                    int f = 0;

                    if( !empty ) avgC = AvgC ( stack , oldQuads );

                    Float gray = 0.2126f * avgC.getRed()
                               + 0.7152f * avgC.getGreen()
                               + 0.0722f * avgC.getBlue();

                    float[] hsb = new float[3];
                    Color.RGBtoHSB( avgC.getRed() , avgC.getGreen() , avgC.getBlue() , hsb );

                    if( hsb[2] < 0.30 ) {
                        //avgC = avgC.brighter().brighter().brighter().brighter();
                        hsb[1] = (float) Math.sqrt( hsb[1] );
                        hsb[2] = hsb[2] + 0.5f;
                        avgC = new Color( Color.HSBtoRGB( hsb[0] , hsb[1] , hsb[2]) );
                    }
                    else {

                        if( !empty ) avgC = avgC.darker().darker().darker();
                        if(  empty ) avgC = avgC.darker().darker();

                    }

                    BakedQuad back = RetexQ( square , "back"  );
                              back = ColorQ( back   , avgC   );
                              back = PushQ ( back   , -0.001f );

                    quads.add( back );

                    for( int i = 0; i < oldQuads.size(); i++ ) {
                        BakedQuad quad = oldQuads.get( i );

                        ItemColors colors = Minecraft.getMinecraft().getItemColors();
                    //------------------------------------------------------------------------------

                        Integer color = colors.getColorFromItemstack( stack , quad.getTintIndex() );

                        int R = (color >> 16) & 255;
                        int G = (color >> 8) & 255;
                        int B = (color >> 0) & 255;

                        //quads.add( TurnQ( Colorize( quad , new Color( R , G , B , 255 ) ) , side) );
                        //quads.add( TurnQ( quad , side) );

                        if( !empty ) quads.add(
                                        ColorQ(
                                            RetexQ( square ,
                                                    quad,
                                                    sideless
                                            ),
                                            new Color( R , G , B , 255)
                                        )
                                    );

                        if( empty ) quads.add( quad );


                        //if( stack.getItem() instanceof ItemBlock )
                        //    quads.add( Colorize( quad , new Color( R , G , B , 255 ) ) );

                        //if( !(stack.getItem() instanceof ItemBlock) )
                        //    quads.add( Colorize( Retexture( square , quad) , new Color( R , G ,
                        //B , 255 )));


                    }

                    //quads.add( PushQ( Retexture( square , "frame" + height ) , 0.001f ) );

                    this.quads.put( side , quads );
                    this.allQuads.addAll( quads );

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
        // ICustomModelLoader
        //==========================================================================================

            public static Boolean finished = false;

        //==========================================================================================

            @Override public boolean accepts( ResourceLocation modelLocation ) {
            //--------------------------------------------------------------------------------------
                if( finished ) return false;
            //--------------------------------------------------------------------------------------

                return modelLocation.getResourceDomain().equals( Base.modId );

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public IModel loadModel(ResourceLocation modelLocation) throws Exception {
            //--------------------------------------------------------------------------------------

                return this;

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public void onResourceManagerReload( IResourceManager resourceManager ) {
            //--------------------------------------------------------------------------------------

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
        // IModel
        //==========================================================================================

            @Override public IBakedModel bake(
            //--------------------------------------------------------------------------------------
                      IModelState  state
                    , VertexFormat format
                    , Function< ResourceLocation , TextureAtlasSprite > bakedTextureGetter
            //--------------------------------------------------------------------------------------
            ) { try {
            //--------------------------------------------------------------------------------------

                if( null != Compressed.base ) return this;

            //--------------------------------------------------------------------------------------
                this.finished = true;
            //--------------------------------------------------------------------------------------

                ResourceLocation      RL       = Blocks.compressed.getRegistryName();
                ModelResourceLocation MRL      = new ModelResourceLocation( RL , "inventory" );
                IModel                fallback = ModelLoaderRegistry.getModel( MRL );

            //--------------------------------------------------------------------------------------

                this.base = fallback.bake( state , format , bakedTextureGetter );

            //--------------------------------------------------------------------------------------
                this.finished = false;
            //--------------------------------------------------------------------------------------

                return this;

            //--------------------------------------------------------------------------------------
            } catch ( Exception ex ) { ex.printStackTrace(); return this; } }

        //==========================================================================================
        // ItemOverrideList
        //==========================================================================================

            public static ItemOverrideList overrides = new ItemOverrideList(new ArrayList<>()){

            //======================================================================================

                @Override public IBakedModel handleItemState(IBakedModel m, ItemStack        s
                                                            ,World       w, EntityLivingBase e){
                //----------------------------------------------------------------------------------
                   // org.lwjgl.input.Mouse.setGrabbed( false );
                //----------------------------------------------------------------------------------
                    if( !s.hasTagCompound() ) return m;
                //----------------------------------------------------------------------------------
                    NBTTagCompound tag = s.getTagCompound();
                //----------------------------------------------------------------------------------

                    String ID = "" + tag.getInteger( "Width" ) + tag.getInteger    ( "Height" )
                                   + tag.getString ( "Mod"   ) + tag.getString     ( "Entry"  )
                                   + tag.getInteger( "Meta"  ) + tag.getCompoundTag( "NBT"    );

                    ID = ID.toLowerCase();

                //----------------------------------------------------------------------------------
                    if( !models.containsKey( ID ) ) models.put( ID , new Compressed( s ) );
                //----------------------------------------------------------------------------------

                    return models.get( ID );

                //----------------------------------------------------------------------------------
                }

            //======================================================================================

            };

            @Override public ItemOverrideList getOverrides() {
            //--------------------------------------------------------------------------------------

                return overrides;

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
        // IBakedModel
        //==========================================================================================

            @Override public List<BakedQuad> getQuads (
            //--------------------------------------------------------------------------------------
                    @Nullable IBlockState state ,
                    @Nullable EnumFacing  side  ,
                              long        rand
            //--------------------------------------------------------------------------------------
            ) {
            //--------------------------------------------------------------------------------------
                //org.lwjgl.input.Mouse.setGrabbed( false );
            //--------------------------------------------------------------------------------------

                // Napraviti da se gleda da li je poziv iz RenderItem.renderModel
                //      Thread.currentThread().getStackTrace();
                // Ako je, vratiti prazni niz te prije vraćanja nacrtati predmet baš kao što bi
                // RenderItem.renderModel nacrtao

                if( null == side )
                    if( null != this.allQuads )
                        if( !this.allQuads.isEmpty() )
                            return this.allQuads;

                if( null == side )
                    if( null != this.allQuads )
                        if( this.allQuads.isEmpty() )
                            return this.base.getQuads( state , side , rand );

                if( !this.quads.containsKey( side ) )
                    return this.base.getQuads( state , side , rand );

                return this.quads.get( side );

            //--------------------------------------------------------------------------------------
            }

            @Override public Pair<IBakedModel, Matrix4f> handlePerspective( TransformType type ) {
            //--------------------------------------------------------------------------------------
                // org.lwjgl.input.Mouse.setGrabbed( false );
            //--------------------------------------------------------------------------------------
                Block clay = net.minecraft.init.Blocks.CLAY;
                ItemStack holder = new ItemStack( Item.getItemFromBlock( clay ) , 1 , 0 );
            //--------------------------------------------------------------------------------------

                return new ImmutablePair<>( this , Minecraft.getMinecraft()
                                                            .getRenderItem()
                                                            .getItemModelMesher()
                                                            .getItemModel( holder )
                                                            .handlePerspective( type ).getRight() );

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public boolean isAmbientOcclusion() {
            //--------------------------------------------------------------------------------------
                return Compressed.base.isAmbientOcclusion();
            //--------------------------------------------------------------------------------------
            }

            @Override public boolean isGui3d() {
            //--------------------------------------------------------------------------------------
                return Compressed.base.isGui3d();
            //--------------------------------------------------------------------------------------
            }

            @Override public boolean isBuiltInRenderer() {
            //--------------------------------------------------------------------------------------
                return Compressed.base.isBuiltInRenderer();
            //--------------------------------------------------------------------------------------
            }

            @Override public TextureAtlasSprite getParticleTexture() {
            //--------------------------------------------------------------------------------------
                return Compressed.base.getParticleTexture();
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

        }


    //==============================================================================================





























    //==============================================================================================
        public static Map<String , IBakedModel> items = new HashMap<>();
    //==============================================================================================


        public static class Overrides extends ItemOverrideList {

        //==========================================================================================

            public Overrides( List<ItemOverride> overrides ) {
            //--------------------------------------------------------------------------------------
                super( overrides );
            //--------------------------------------------------------------------------------------

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
        // Entry point for items
        //==========================================================================================

            @Override public IBakedModel handleItemState( IBakedModel                originalModel
                                                        , ItemStack                  stack
                                                        , @Nullable World            world
                                                        , @Nullable EntityLivingBase entity ) {
            //--------------------------------------------------------------------------------------
                //org.lwjgl.input.Mouse.setGrabbed( false );
            //--------------------------------------------------------------------------------------

                if( !stack.hasTagCompound() ) return originalModel;

            //--------------------------------------------------------------------------------------

                String         mod   = stack.getTagCompound().getString( "Mod" );
                String         entry = stack.getTagCompound().getString( "Entry" );
                Integer        meta  = stack.getTagCompound().getInteger( "Meta" );
                NBTTagCompound nbt   = stack.getTagCompound().getCompoundTag( "NBT" );

            //--------------------------------------------------------------------------------------
                String ID = mod + entry + meta + nbt;
            //--------------------------------------------------------------------------------------

                if( items.containsKey( ID ) ) return items.get( ID );

            //--------------------------------------------------------------------------------------
                List<ItemStack> stacks = new ArrayList<>( Configurations.entries );
                stacks.removeIf( s -> !s.getItem()
                                        .getRegistryName()
                                        .getResourceDomain()
                                        .equals( mod ) );

                stacks.removeIf( s -> !s.getItem()
                                        .getRegistryName()
                                        .getResourcePath()
                                        .equals( entry ) );

                stacks.removeIf( s -> !meta.equals( s.getMetadata() ) );

                stacks.removeIf( s -> !s.getTagCompound()
                                        .toString()
                                        .replace( " " , "" )
                                        .toLowerCase()
                                        .contains(   nbt.toString()
                                                        .replace( " " , "" )
                                                        .toLowerCase() ) );

            //--------------------------------------------------------------------------------------
                if( 1 != stacks.size() ) return originalModel;
            //--------------------------------------------------------------------------------------

                IBakedModel model = Minecraft.getMinecraft()
                        .getRenderItem()
                        .getItemModelMesher()
                        .getItemModel( stacks.get(0) );

                items.put( ID , new CompressedBakedModel( stacks.get(0) , model ,
                        CompressedModelState ) );

            //--------------------------------------------------------------------------------------
                return items.get( ID );
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

        }


    //==============================================================================================
        public static Overrides overrides = new Overrides( Collections.EMPTY_LIST );
    //==============================================================================================


        public static class CompressedBakedModel extends PerspectiveMapWrapper {

        //==========================================================================================

            public ItemStack   baseStack;
            public IBakedModel baseModel;

            public List<BakedQuad> quads = new ArrayList<>();

            public Map<EnumFacing , List<BakedQuad>> sides = new HashMap<>();

        //==========================================================================================

            CompressedBakedModel( ItemStack base , IBakedModel baseModel , IModelState state ) {
            //--------------------------------------------------------------------------------------
                super( baseModel , state );
            //--------------------------------------------------------------------------------------

                this.baseStack = base;
                this.baseModel = baseModel;

            //--------------------------------------------------------------------------------------
            }

//            CompressedBakedModel( ItemStack base , IModelState state ) {
//            //--------------------------------------------------------------------------------------
//                super( Minecraft.getMinecraft()
//                                .getRenderItem()
//                                .getItemModelMesher()
//                                .getItemModel( base ) , state );
//            //--------------------------------------------------------------------------------------
//
//                this.baseStack = base;
//                this.baseModel = new PerspectiveMapWrapper( Minecraft.getMinecraft()
//                                                                      .getRenderItem()
//                                                                      .getItemModelMesher()
//                                                                      .getItemModel( baseStack )
//                                                           , state );
//
//            //--------------------------------------------------------------------------------------
//            }

        //==========================================================================================

            public List<BakedQuad> getItemQuads ( long rand ) {
            //--------------------------------------------------------------------------------------
                if( !this.quads.isEmpty() ) return this.quads;
            //--------------------------------------------------------------------------------------

                IBakedModel model = ForgeHooksClient.handleCameraTransforms(
                        baseModel , TransformType.GUI , false );

            //----------------------------------------------------------------------------------
                for( BakedQuad quad : model.getQuads( null , null, rand ) ) {
            //----------------------------------------------------------------------------------

                    Integer color = Minecraft.getMinecraft()
                            .getItemColors()
                            .getColorFromItemstack(baseStack
                                    , quad.getTintIndex());

                //----------------------------------------------------------------------------------

                    int[] data = new int[quad.getVertexData().length];

                    for( int i = 0; i < data.length; i++ )
                        data[i] = quad.getVertexData()[i];

                //----------------------------------------------------------------------------------

                    BakedQuad newQuad = new BakedQuad( data
                                                     , quad.getTintIndex()
                                                     , quad.getFace()
                                                     , quad.getSprite()
                                                     , quad.shouldApplyDiffuseLighting()
                                                     , quad.getFormat());

                //------------------------------------------------------------------------------
                    this.quads.add(newQuad);
                //------------------------------------------------------------------------------

                    if( -1 == color ) continue;

                //------------------------------------------------------------------------------
                    for (int i = 0, s = 0; i < newQuad.getFormat().getElementCount(); i++) {
                //------------------------------------------------------------------------------

                        VertexFormatElement elem = newQuad.getFormat().getElement(i);

                    //--------------------------------------------------------------------------
                        if (elem.getUsage().equals(VertexFormatElement.EnumUsage.COLOR)) {
                    //--------------------------------------------------------------------------

                            int A1 = newQuad.getVertexData()[s / 4 + 7 * 0] >> 24;
                            int A2 = newQuad.getVertexData()[s / 4 + 7 * 1] >> 24;
                            int A3 = newQuad.getVertexData()[s / 4 + 7 * 2] >> 24;
                            int A4 = newQuad.getVertexData()[s / 4 + 7 * 3] >> 24;

                            int R = (color >> 16) & 255;
                            int G = (color >> 8) & 255;
                            int B = (color >> 0) & 255;
                            int col = (B << 16) + (G << 8) + (R << 0);

                            newQuad.getVertexData()[s / 4 + 7 * 0] = (col << 0) + (A1 << 24);
                            newQuad.getVertexData()[s / 4 + 7 * 1] = (col << 0) + (A2 << 24);
                            newQuad.getVertexData()[s / 4 + 7 * 2] = (col << 0) + (A3 << 24);
                            newQuad.getVertexData()[s / 4 + 7 * 3] = (col << 0) + (A4 << 24);

                    //--------------------------------------------------------------------------
                        }
                    //--------------------------------------------------------------------------

                        s += elem.getSize();

            //--------------------------------------------------------------------------------------
                } } return this.quads;
            //--------------------------------------------------------------------------------------
            }

            public List<BakedQuad> getBlockQuads( EnumFacing  side , long rand ) {
            //--------------------------------------------------------------------------------------
                if( this.sides.containsKey( side ) ) return this.sides.get( side );
            //--------------------------------------------------------------------------------------

                Block       block = Block.getBlockFromItem( this.baseStack.getItem() );
                IBlockState state = block.getBlockState().getBaseState();

            //--------------------------------------------------------------------------------------

                IBakedModel model = ForgeHooksClient.handleCameraTransforms(
                    baseModel , TransformType.GUI , false );

            //--------------------------------------------------------------------------------------

                List<BakedQuad> newQuads = new ArrayList<>();
                List<BakedQuad> oldQuads = model.getQuads( null , side, rand ) ;

            //--------------------------------------------------------------------------------------
                for( BakedQuad quad : oldQuads ) {
            //--------------------------------------------------------------------------------------

                    Integer color = Minecraft.getMinecraft()
                                             .getItemColors()
                                             .getColorFromItemstack( baseStack
                                                                   , quad.getTintIndex());

                //----------------------------------------------------------------------------------

                    int[] data = new int[quad.getVertexData().length];

                    for( int i = 0; i < data.length; i++ )
                        data[i] = quad.getVertexData()[i];

                //----------------------------------------------------------------------------------

                    BakedQuad newQuad = new BakedQuad( data
                            , quad.getTintIndex()
                            , quad.getFace()
                            , quad.getSprite()
                            , quad.shouldApplyDiffuseLighting()
                            , quad.getFormat());

                //------------------------------------------------------------------------------
                    newQuads.add(newQuad);
                //------------------------------------------------------------------------------

                    if( -1 == color ) continue;

                //------------------------------------------------------------------------------
                    for (int i = 0, s = 0; i < newQuad.getFormat().getElementCount(); i++) {
                //------------------------------------------------------------------------------

                        VertexFormatElement elem = newQuad.getFormat().getElement(i);

                    //--------------------------------------------------------------------------
                        if (elem.getUsage().equals(VertexFormatElement.EnumUsage.COLOR)) {
                    //--------------------------------------------------------------------------

                            int A1 = newQuad.getVertexData()[s / 4 + 7 * 0] >> 24;
                            int A2 = newQuad.getVertexData()[s / 4 + 7 * 1] >> 24;
                            int A3 = newQuad.getVertexData()[s / 4 + 7 * 2] >> 24;
                            int A4 = newQuad.getVertexData()[s / 4 + 7 * 3] >> 24;

                            int R = (color >> 16) & 255;
                            int G = (color >> 8) & 255;
                            int B = (color >> 0) & 255;
                            int col = (B << 16) + (G << 8) + (R << 0);

                            newQuad.getVertexData()[s / 4 + 7 * 0] = (col << 0) + (A1 << 24);
                            newQuad.getVertexData()[s / 4 + 7 * 1] = (col << 0) + (A2 << 24);
                            newQuad.getVertexData()[s / 4 + 7 * 2] = (col << 0) + (A3 << 24);
                            newQuad.getVertexData()[s / 4 + 7 * 3] = (col << 0) + (A4 << 24);

                    //--------------------------------------------------------------------------
                        }
                    //--------------------------------------------------------------------------

                        s += elem.getSize();

                //----------------------------------------------------------------------------------
                    } }
                //----------------------------------------------------------------------------------

                    this.sides.put( side , newQuads );

            //----------------------------------------------------------------------------------
                return this.quads;
            //----------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public ItemOverrideList getOverrides() { return overrides; }

            @Override public List<BakedQuad>  getQuads( @Nullable IBlockState inState ,
                                                        @Nullable EnumFacing  side  ,
                                                                  long        rand  ) {
            //--------------------------------------------------------------------------------------
                //org.lwjgl.input.Mouse.setGrabbed( false );
            //--------------------------------------------------------------------------------------
                if( baseStack.hasTagCompound() ) {
            //--------------------------------------------------------------------------------------

                    //Pair<IBakedModel , Matrix4f> tr = this.handlePerspective( TransformType.GUI );
                    //ForgeHooksClient.multiplyCurrentGlMatrix( tr.getRight() );

                    if( !( this.baseStack.getItem() instanceof ItemBlock ) )
                        return getItemQuads( rand );

                    if(  ( this.baseStack.getItem() instanceof ItemBlock ) )
                        return getBlockQuads( side , rand );

//                    Block block = Block.getBlockFromItem( this.baseStack.getItem() );
//
//                    if( !this.quads.isEmpty() && Blocks.AIR.equals( block ) ) return this.quads;
//
//                //--------------------------------------------------------------------------------------
//
//                    // 248, 36, 35
//                    // 205, 92, 171
//                    // 147, 36, 35
//                //----------------------------------------------------------------------------------
//
//                    Pair<IBakedModel , Matrix4f> tr = this.handlePerspective( TransformType.GUI );
//                    ForgeHooksClient.multiplyCurrentGlMatrix( tr.getRight() );
//
//                    IBakedModel model = ForgeHooksClient.handleCameraTransforms(
//                            baseModel , TransformType.GUI , false );
//
//                //----------------------------------------------------------------------------------
//
//                    List<BakedQuad>      prev = model.getQuads(block.getDefaultState(), side, rand);
//                    if( prev.isEmpty() ) prev = model.getQuads( null , null , rand );
//
//                //----------------------------------------------------------------------------------
//                    for( BakedQuad quad : prev ) {
//                //----------------------------------------------------------------------------------
//
//                        Integer color = Minecraft.getMinecraft()
//                                                 .getItemColors()
//                                                 .getColorFromItemstack( baseStack
//                                                                       , quad.getTintIndex() );
//
//                        int[] data = new int[ quad.getVertexData().length ];
//                        for( int i = 0; i < data.length; i++ ) data[i] = quad.getVertexData()[i];
//
//                        BakedQuad newQuad = new BakedQuad( data
//                                                         , quad.getTintIndex()
//                                                         , quad.getFace()
//                                                         , quad.getSprite()
//                                                         , quad.shouldApplyDiffuseLighting()
//                                                         , quad.getFormat() );
//
//                    //------------------------------------------------------------------------------
//                        this.quads.add( newQuad );
//                    //------------------------------------------------------------------------------
//
//                        if( -1 == color ) continue;
//
//                    //------------------------------------------------------------------------------
//                        for( int i = 0, s = 0; i < newQuad.getFormat().getElementCount(); i++ ) {
//                    //------------------------------------------------------------------------------
//
//                            VertexFormatElement elem = newQuad.getFormat().getElement( i );
//
//                        //--------------------------------------------------------------------------
//                            if( elem.getUsage().equals( VertexFormatElement.EnumUsage.COLOR ) ) {
//                        //--------------------------------------------------------------------------
//
//                                int A1 = newQuad.getVertexData()[s / 4 + 7 * 0] >> 24;
//                                int A2 = newQuad.getVertexData()[s / 4 + 7 * 1] >> 24;
//                                int A3 = newQuad.getVertexData()[s / 4 + 7 * 2] >> 24;
//                                int A4 = newQuad.getVertexData()[s / 4 + 7 * 3] >> 24;
//
//                                int R = (color >> 16) & 255;
//                                int G = (color >> 8 ) & 255;
//                                int B = (color >> 0 ) & 255;
//                                int col = (B << 16) + (G << 8) + (R << 0);
//
//                                newQuad.getVertexData()[s / 4 + 7 * 0] = (col << 0) + (A1 << 24);
//                                newQuad.getVertexData()[s / 4 + 7 * 1] = (col << 0) + (A2 << 24);
//                                newQuad.getVertexData()[s / 4 + 7 * 2] = (col << 0) + (A3 << 24);
//                                newQuad.getVertexData()[s / 4 + 7 * 3] = (col << 0) + (A4 << 24);
//
//                        //--------------------------------------------------------------------------
//                            }
//                        //--------------------------------------------------------------------------
//
//                            s += elem.getSize();
//
//                    //------------------------------------------------------------------------------
//                        }
//                    //------------------------------------------------------------------------------
//
//
//                //----------------------------------------------------------------------------------
//                    }
//                //----------------------------------------------------------------------------------
//
//                    return this.quads;
                    //return model.getQuads( block.getDefaultState() , side , rand );

            //--------------------------------------------------------------------------------------
                } if( null != inState ) {
            //--------------------------------------------------------------------------------------

                    IExtendedBlockState state = (IExtendedBlockState) inState;

                    Integer posX = null;
                    Integer posY = null;
                    Integer posZ = null;

                    for (IUnlistedProperty prop : state.getUnlistedNames()) {
                        if (prop.getName().equals("PosX")) posX = (Integer) state.getValue(prop);
                        if (prop.getName().equals("PosY")) posY = (Integer) state.getValue(prop);
                        if (prop.getName().equals("PosZ")) posZ = (Integer) state.getValue(prop);
                    }

                    BlockPos position = new BlockPos( posX , posY , posZ );

                //----------------------------------------------------------------------------------

                    Integer dimID = Minecraft.getMinecraft().world.provider.getDimension();
                    ItemStack placed = Blocks.placed.get(dimID).get(position);
                    IBakedModel model = overrides.handleItemState(this, placed, null, null);

                    return model.getQuads(inState, side, rand);

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------

                Block gravelB = net.minecraft.init.Blocks.GRAVEL;
                ItemStack gravel = new ItemStack( Item.getItemFromBlock( gravelB ) , 1 , 0 );

                return Minecraft.getMinecraft()
                        .getRenderItem()
                        .getItemModelMesher()
                        .getItemModel( gravel ).getQuads( null, side , rand );

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public Pair<IBakedModel, Matrix4f> handlePerspective(TransformType type) {
            //--------------------------------------------------------------------------------------
                //org.lwjgl.input.Mouse.setGrabbed( false );
            //--------------------------------------------------------------------------------------

                Vector3f rotation    = new Vector3f( 0 , 0 , 0 );
                Vector3f translation = new Vector3f( 0 , 0 , 0 );
                Vector3f scale       = new Vector3f( 1 , 1 , 1 );

            //--------------------------------------------------------------------------------------

                ItemCameraTransforms transforms = this.getItemCameraTransforms();

                switch( type ) {
                //----------------------------------------------------------------------------------
                    case GUI:
                //----------------------------------------------------------------------------------

                        rotation    = transforms.gui.rotation;
                        translation = transforms.gui.translation;
                        scale       = transforms.gui.scale;

                //----------------------------------------------------------------------------------
                    break; case FIRST_PERSON_LEFT_HAND:
                //----------------------------------------------------------------------------------

                        rotation    = transforms.firstperson_left.rotation;
                        translation = transforms.firstperson_left.translation;
                        scale       = transforms.firstperson_left.scale;

                //----------------------------------------------------------------------------------
                    break; case FIRST_PERSON_RIGHT_HAND:
                //----------------------------------------------------------------------------------

                        rotation    = transforms.firstperson_right.rotation;
                        translation = transforms.firstperson_right.translation;
                        scale       = transforms.firstperson_right.scale;

                //----------------------------------------------------------------------------------
                    break; case FIXED:
                //----------------------------------------------------------------------------------

                        rotation    = transforms.fixed.rotation;
                        translation = transforms.fixed.translation;
                        scale       = transforms.fixed.scale;

                //----------------------------------------------------------------------------------
                    break; case GROUND:
                //----------------------------------------------------------------------------------

                        rotation    = transforms.ground.rotation;
                        translation = transforms.ground.translation;
                        scale       = transforms.ground.scale;

                //----------------------------------------------------------------------------------
                    break; case HEAD:
                //----------------------------------------------------------------------------------

                        rotation    = transforms.head.rotation;
                        translation = transforms.head.translation;
                        scale       = transforms.head.scale;

                //----------------------------------------------------------------------------------
                    break; case THIRD_PERSON_RIGHT_HAND:
                //----------------------------------------------------------------------------------

                        rotation    = transforms.thirdperson_right.rotation;
                        translation = transforms.thirdperson_right.translation;
                        scale       = transforms.thirdperson_right.scale;

                //----------------------------------------------------------------------------------
                    break; case THIRD_PERSON_LEFT_HAND:
                //----------------------------------------------------------------------------------

                        rotation    = transforms.thirdperson_left.rotation;
                        translation = transforms.thirdperson_left.translation;
                        scale       = transforms.thirdperson_left.scale;

                //----------------------------------------------------------------------------------
                }

            //--------------------------------------------------------------------------------------

                Matrix4f rotX = new Matrix4f();
                Matrix4f rotY = new Matrix4f();
                Matrix4f rotZ = new Matrix4f();

                rotX.setIdentity();
                rotY.setIdentity();
                rotZ.setIdentity();

                rotX.rotX( (float) Math.toRadians( rotation.getX() ) );
                rotY.rotY( (float) Math.toRadians( rotation.getY() ) );
                rotZ.rotZ( (float) Math.toRadians( rotation.getZ() ) );

            //--------------------------------------------------------------------------------------

                Matrix4f scaleM = new Matrix4f();
                Matrix4f transM = new Matrix4f();

                scaleM.setIdentity();
                transM.setIdentity();

                scaleM.setM00( scale.getX() );
                scaleM.setM11( scale.getY() );
                scaleM.setM22( scale.getZ() );

                transM.setM03( translation.getX() );
                transM.setM13( translation.getY() );
                transM.setM23( translation.getZ() );

            //--------------------------------------------------------------------------------------
                Matrix4f matrix = new Matrix4f();
            //--------------------------------------------------------------------------------------

                matrix.setIdentity();

                matrix.mul( rotX );
                matrix.mul( rotY );
                matrix.mul( rotZ );

                matrix.mul( scaleM );
                matrix.mul( transM );

            //--------------------------------------------------------------------------------------
                return new ImmutablePair<>( this , matrix );
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

        }


    //==============================================================================================


        public static final class CompressedModel implements IModel {

        //==========================================================================================
            @Override
        //==========================================================================================

            public IBakedModel bake( IModelState  state
                                   , VertexFormat format
                                   , Function< ResourceLocation
                                             , TextureAtlasSprite > bakedTextureGetter ) { try {
            //--------------------------------------------------------------------------------------
                Block gravelB = net.minecraft.init.Blocks.GRAVEL;
                ItemStack gravel = new ItemStack( Item.getItemFromBlock( gravelB ) , 1 , 0 );
            //--------------------------------------------------------------------------------------

                IBakedModel model = ModelLoaderRegistry.getMissingModel()
                                                             .bake( state , format ,
                                                                    bakedTextureGetter );

                CompressedModelState = state;

                return new CompressedBakedModel( gravel , model , state );

            //--------------------------------------------------------------------------------------
            } catch ( Exception ex ) { ex.printStackTrace(); return null; } }

        //==========================================================================================
    }


    //==============================================================================================
        public static IModelState CompressedModelState = null;
    //==============================================================================================


        public static class CompressedModelLoader implements ICustomModelLoader {
        //==========================================================================================
            public static Boolean finished = false;
        //==========================================================================================

            @Override public boolean accepts( ResourceLocation modelLocation ) {
            //--------------------------------------------------------------------------------------
                if( finished ) return false;
            //--------------------------------------------------------------------------------------

                return modelLocation.getResourceDomain().equals( Base.modId );

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public IModel loadModel( ResourceLocation modelLocation ) throws Exception {
            //--------------------------------------------------------------------------------------

                return new CompressedModel();

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public void onResourceManagerReload( IResourceManager resourceManager ) {
            //--------------------------------------------------------------------------------------

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

        }


    //==============================================================================================

    //==============================================================================================
    // IBakedModel section
    //==============================================================================================
        /*
        @Override public List<BakedQuad> getQuads( @Nullable IBlockState state ,
                                                   @Nullable EnumFacing side   ,
                                                             long rand         ) {

            //org.lwjgl.input.Mouse.setGrabbed(false);

            Item gravel = Item.getItemFromBlock( Blocks.GRAVEL );

            ItemStack stack = null == this.stack ? new ItemStack( gravel , 1 , 0 ) : this.stack;

            if( state instanceof IExtendedBlockState ) {

                IExtendedBlockState exstate = (IExtendedBlockState) state;

                Integer posx = null;
                Integer posy = null;
                Integer posz = null;

                for( IUnlistedProperty prop : exstate.getUnlistedNames() ) {
                    if( prop.getName().equals( "PosX") ) posx = (Integer) exstate.getValue( prop );
                    if( prop.getName().equals( "PosY") ) posy = (Integer) exstate.getValue( prop );
                    if( prop.getName().equals( "PosZ") ) posz = (Integer) exstate.getValue( prop );
                }

                BlockPos pos      = new BlockPos( posx , posy, posz );
                Position position = new Position( posx , posy , posz);

                if( Items.placed.containsKey( position ) )
                    if( Items.placed.get( position ).getLeft().isAirBlock( pos ) )
                        Items.placed.remove( position );

                ItemStack newStack = Items.placed.getOrDefault( position , new ImmutablePair<>
                        (null , null) ).getRight();

                if( null != newStack ) {

                    NBTTagCompound tag = newStack.getTagCompound();
                    String  mod   = tag.getString( "Mod" );
                    String  entry = tag.getString( "Entry" );
                    Integer meta  = tag.getInteger( "Meta" );
                    String  nbt   = tag.getString( "NBT" );

                    List<Item> items = new ArrayList<>( ForgeRegistries.ITEMS.getValues() );

                    items.removeIf( s -> !s.getRegistryName().getResourceDomain().equals( mod ) );
                    items.removeIf( s -> !s.getRegistryName().getResourcePath  ().equals( entry ) );

                    for( Item item : items ) {

                        CreativeTabs tab = item.getCreativeTab();
                        if( null == tab ) tab = CreativeTabs.CREATIVE_TAB_ARRAY[0];

                //----------------------------------------------------------------------------------

                        NonNullList<ItemStack> stacks = NonNullList.create();
                        item.getSubItems( tab , stacks );

                        stacks.removeIf( s -> !meta.equals( s.getMetadata() ) );

                        if( !nbt.isEmpty() ) stacks.removeIf( s -> !s.hasTagCompound() );
                        if( !nbt.isEmpty() )
                            stacks.removeIf( s -> !s.getTagCompound().toString()
                                    .replace( " " , "" ).toLowerCase()
                                    .equals( nbt.replace( " " , "" )
                                            .toLowerCase() ) );

                        if( 1 == stacks.size() ) stack = stacks.get( 0 );
                    }
                }

                int h = 0;
            }

            Pair<? extends IBakedModel,Matrix4f> tr = this.handlePerspective(  stack , TransformType
                    .GUI );

            ForgeHooksClient.multiplyCurrentGlMatrix( tr.getRight() );

            return tr.getLeft().getQuads( Block.getBlockFromItem( stack.getItem() )
            .getDefaultState(),
                    side , rand );



            //model = model.getOverrides().handleItemState( model , stack , world , entity );


//            Pair<? extends IBakedModel, Matrix4f> tr = model.handlePerspective(
//                    ItemCameraTransforms.TransformType.GUI );
//
//            ForgeHooksClient.multiplyCurrentGlMatrix( tr.getRight() );
//
//            return tr.getLeft().getQuads( Block.getBlockFromItem(stack.getItem())
.getDefaultState(),
//                    side , rand );

//            model = ForgeHooksClient.handleCameraTransforms( model ,
//                    ItemCameraTransforms.TransformType.GUI , false );
//
//            Matrix4f scale = new Matrix4f();
//            scale.setIdentity();
//            scale.setScale( 1.5f );
//
//            Matrix4f matrix = new Matrix4f();
//            matrix.setIdentity();
//            matrix.mul( scale );
//
//            ForgeHooksClient.multiplyCurrentGlMatrix( matrix );


//
//            Matrix4f translate = new Matrix4f();
//            translate.setIdentity();
//            translate.setTranslation(new Vector3f( 0.0f , -1.0f / 16, -1.0f / 16 ));
//
//            Matrix4f rotX = new Matrix4f();
//            rotX.setIdentity();
//            rotX.setRotation( new AxisAngle4f( 1, 0, 0, (float) Math.toRadians( 360 - 45 ) ) );
//
//            Matrix4f rotY = new Matrix4f();
//            rotY.setIdentity();
//            rotY.setRotation( new AxisAngle4f( 0, 1, 0, (float) Math.toRadians( 360 - 45 ) ) );
//
//            Matrix4f scale = new Matrix4f();
//            scale.setIdentity();
//            scale.setScale( 1.0f - 1.0f / 16 );
//
//            Matrix4f matrix = new Matrix4f();
//            matrix.setIdentity();
//            //matrix.mul( translate );
//            //matrix.mul( scale );
//            //matrix.mul( rotX );
//            //matrix.mul( rotY );
//
//            ForgeHooksClient.multiplyCurrentGlMatrix( matrix );

            //return model.getQuads( Block.getBlockFromItem( stack.getItem() ).getDefaultState(),
            //        side , rand );
        }

        @Override public boolean isAmbientOcclusion() {

            Item gravel = Item.getItemFromBlock( Blocks.GRAVEL );

            ItemStack stack = null == this.stack ? new ItemStack( gravel , 1 , 0 ) : this.stack;

            IBakedModel model = Minecraft.getMinecraft()
                    .getRenderItem()
                    .getItemModelMesher()
                    .getItemModel( stack );

            return model.isAmbientOcclusion();
        }

        @Override public boolean isGui3d() {

            Item gravel = Item.getItemFromBlock( Blocks.GRAVEL );

            ItemStack stack = null == this.stack ? new ItemStack( gravel , 1 , 0 ) : this.stack;

            IBakedModel model = Minecraft.getMinecraft()
                    .getRenderItem()
                    .getItemModelMesher()
                    .getItemModel( stack );

            return model.isGui3d();
        }

        @Override public boolean isBuiltInRenderer() {


            Item gravel = Item.getItemFromBlock( Blocks.GRAVEL );

            ItemStack stack = null == this.stack ? new ItemStack( gravel , 1 , 0 ) : this.stack;

            IBakedModel model = Minecraft.getMinecraft()
                    .getRenderItem()
                    .getItemModelMesher()
                    .getItemModel( stack );

            return model.isBuiltInRenderer();
        }

        @Override public TextureAtlasSprite getParticleTexture() {

            Item gravel = Item.getItemFromBlock( Blocks.GRAVEL );

            ItemStack stack = null == this.stack ? new ItemStack( gravel , 1 , 0 ) : this.stack;

            IBakedModel model = Minecraft.getMinecraft()
                    .getRenderItem()
                    .getItemModelMesher()
                    .getItemModel( stack );

            return model.getParticleTexture();
        }

        @Override public ItemOverrideList getOverrides() {
            //org.lwjgl.input.Mouse.setGrabbed(false);

            return overrrides;
        }

        @Override public Pair<? extends IBakedModel,Matrix4f> handlePerspective(TransformType type){

            return this.handlePerspective( this.stack , type );
        }

        public Pair<? extends IBakedModel,Matrix4f> handlePerspective(
                ItemStack stack ,
                TransformType type) {

            Item gravel = Item.getItemFromBlock( Blocks.GRAVEL );

            stack = null == stack ? new ItemStack( gravel , 1 , 0 ) : stack;

            IBakedModel model = Minecraft.getMinecraft()
                    .getRenderItem()
                    .getItemModelMesher()
                    .getItemModel( stack );

            //org.lwjgl.input.Mouse.setGrabbed(false);

            org.lwjgl.util.vector.Vector3f rot1= ((PerspectiveMapWrapper) model)
                    .getItemCameraTransforms().gui.rotation;

            Matrix4f matrix = new Matrix4f();
            matrix.setIdentity();
            // 30 , 225 , 0
            // 0.5 , 0.5 , 0.5

            if( type.equals( TransformType.GUI ) ) {
                Vector3f rotation = ((PerspectiveMapWrapper) model)
                        .getItemCameraTransforms().gui.rotation;
                Vector3f translation = ((PerspectiveMapWrapper) model)
                        .getItemCameraTransforms().gui.translation;
                Vector3f scale = ((PerspectiveMapWrapper) model)
                        .getItemCameraTransforms().gui.scale;

                Matrix4f rotX = new Matrix4f();
                rotX.setIdentity();
                rotX.rotX( (float) Math.toRadians( rotation.getX() ) );

                Matrix4f rotY = new Matrix4f();
                rotY.setIdentity();
                rotY.rotY( (float) Math.toRadians( rotation.getY() ) );

                Matrix4f mat = new Matrix4f();
                mat.setIdentity();
                mat.mul( rotX );
                mat.mul( rotY );
                mat.setScale( scale.getX() );

                TRSRTransformation tr1 = new TRSRTransformation(mat);

                TRSRTransformation tr2 = TRSRTransformation.blockCornerToCenter( tr1 );
                matrix = tr1.getMatrix();
            }

            return new ImmutablePair<>( model , matrix );
        }
//*/


    //==============================================================================================
    }

//==================================================================================================
