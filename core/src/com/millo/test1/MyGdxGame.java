package com.millo.test1;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;

import javax.print.DocFlavor;

public class MyGdxGame implements ApplicationListener, GestureDetector.GestureListener {

	private final static String TAG = "MyGdxGame";

	private boolean mIsPerspective = true;

	public class MySprite extends MyPackage{
		Sprite _sprite;

		public MySprite(Texture texture, String packagename, String name, Sprite sprite){
			super(texture, packagename, name);
			_sprite = sprite;
		}
	}

	Camera cam;
//	OrthographicCamera cam;
//  PerspectiveCamera cam;

    ArrayList<MyPackage> icons = new ArrayList<MyPackage>();
    ArrayList<MySprite> spriteIcons = new ArrayList<MySprite>();

    //BitmapFont font;
    BitmapFont font12;
    GlyphLayout glyphLayout;

    SpriteBatch batch;
    //Texture texture;
    //Sprite sprite;

	private Mesh squareMesh;
    //private DefaultShader shader;
    private ShaderProgram shaderProgram;

    String vertexShader =
            "attribute vec4 a_position;    \n" +
            "attribute vec4 a_color;\n" +
            "attribute vec2 a_texCoord0;\n" +
            "uniform mat4 u_worldView;\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "void main()                  \n" +
            "{                            \n" +
            "   v_color = vec4(1, 1, 1, 1); \n" +
            "   v_texCoords = a_texCoord0; \n" +
            "   gl_Position =  u_worldView * a_position;  \n"      +
            "}                            \n" ;
    String fragmentShader =
            "#ifdef GL_ES\n" +
            "precision mediump float;\n" +
            "#endif\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "uniform sampler2D u_texture;\n" +
            "void main()                                  \n" +
            "{                                            \n" +
            "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" +
            "}";

    int WX = 10;

	public interface MyAndroidPackageManager {
		//public Texture getIcon();
        public ArrayList<MyPackage> getIcons();
		public void logd(String _TAG, String s);
	}

	MyAndroidPackageManager _mapm;

	public MyGdxGame(MyAndroidPackageManager mapm){
		_mapm = mapm;
	}

    private float getViewportWidth(){
        return 1000f;
    }
    private float getViewportHeight(float screenW, float screenH){
        return 1000 * (screenH / screenW);
    }
    private float getFieldOfView(){
        return 67;
    }

    private void ResetCamera(Camera cam){
        if (!mIsPerspective) {
            cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
        }
        else {
            cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 800f);
            cam.lookAt(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
            cam.near = 0.1f;
            cam.far = 2000f;
        }
    }

    @Override
	public void create () {
		icons = _mapm.getIcons(); //new Texture("badlogic.jpg");

//        PixmapPacker packer = new PixmapPacker(512, 512, Pixmap.Format.RGB565, 2, true);
//
//        for(int i=0; i<icons.size(); i++) {
//            Texture tex = icons.get(i);
//            packer.pack("texture"+i, tex.getTextureData().consumePixmap());
//        }
//        TextureAtlas atlas = packer.generateTextureAtlas(TextureFilter.Linear, TextureFilter.Linear, false);
//
//        PixmapPackerIO io = new PixmapPackerIO();
//        try {
//            io.save(Gdx.files.internal("data/icons.atlas"), packer);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        packer.dispose();
//        // ...
//        atlas.dispose();

//        camera = new OrthographicCamera(1280, 720);
//		batch = new SpriteBatch();
		//texture = new Texture(Gdx.files.internal("data/Toronto2048wide.jpg"));

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

		if (!mIsPerspective) {
			cam = new OrthographicCamera(getViewportWidth(), getViewportHeight(w,h));
			//cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
		}
		else
		{
			cam = new PerspectiveCamera(getFieldOfView(), getViewportWidth(), getViewportHeight(w,h));
			//cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 200f);
			//cam.lookAt(cam.viewportWidth / 2f, cam.viewportHeight / 2f,0);
			//cam.near = 0.1f;
			//cam.far = 300f;
//        cam = new PerspectiveCamera(60, 1000, 1000 * (h / w));
//        cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 10);
//        cam.lookAt(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
		}
        ResetCamera(cam);
        cam.update();
        batch = new SpriteBatch();

        //font = new BitmapFont();

        int fontSize = (int)(10 * Gdx.graphics.getDensity());
        Gdx.app.log(TAG, "Font size: "+fontSize+"px");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = fontSize;
        font12 = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose(); // don't forget to dispose to avoid memory leaks!


//        Texture texture = new Texture(Gdx.files.internal("arial.png"), true); // true enables mipmaps
//        texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear); // linear filtering in nearest mipmap image
//        font12 = new BitmapFont(Gdx.files.internal("arial.fnt"), new TextureRegion(texture), false);
        //font12.getData().setScale(0.01f, 0.01f*h / w);

        glyphLayout = new GlyphLayout(font12, "", Color.BLUE, 1, Align.center, true);

        //float y = 0f;
        int count = 0;
        for(MyPackage mp: icons) {

            Texture t = mp._texture;
            Sprite sprite = new Sprite(t);
            sprite.setOrigin(0, 0);

            float xu = count%WX;
            float yu = (count-xu)/WX;
            float x = 500f + xu*(getViewportWidth()/WX);
            float y = 1000f - yu*(getViewportHeight(w,h)/WX);
            float sw = 1000f/WX;
            float sh = 1000f/WX;
            _mapm.logd(TAG, mp._packagename +" "+ mp._name);
            _mapm.logd(TAG, "count:" + count + "    xu:"+xu+"   yu:"+yu+"   x:" + x + "     y:" + y + "     sw:"+sw + "     sh:"+sh);
            sprite.setPosition(x, y); //-sprite.getWidth() / 2, -sprite.getHeight() / 2 + y);
//            sprite.setSize(1000f/WX, 1000f/WX);
			sprite.setSize(sw, sh);

			MySprite ms = new MySprite(mp._texture, mp._packagename, mp._name, sprite);

            spriteIcons.add(ms);
            count++;

            //y += 0.1f;
            //break;
        }

		Gdx.input.setInputProcessor(new GestureDetector(this));

//		squareMesh = new Mesh(true, 4, 4,
//				new VertexAttribute( VertexAttributes.Usage.Position, 3, "a_position"),
//				new VertexAttribute( VertexAttributes.Usage.ColorPacked, 4, "a_color"));

        squareMesh = new Mesh( true, 4, 4,
                new VertexAttribute( VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE ),
                new VertexAttribute( VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE+"0" ) );

        squareMesh.setVertices(new float[]{
                -1f, -1f, 0, 0, 1, //Color.toFloatBits(0, 128, 0, 255),
                1f, -1f, 0, 1, 1, //Color.toFloatBits(0, 192, 0, 255),
                -1f, 1f, 0, 0, 0, //Color.toFloatBits(0, 192, 0, 255),
                1f, 1f, 0, 1, 0 //Color.toFloatBits(0, 255, 0, 255)
        });
		squareMesh.setIndices(new short[]{0, 1, 2, 3});
        squareMesh.scale(50, 50, 0);
        squareMesh.transform(new Matrix4().translate(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0));
        //squareMesh.transform(new Matrix4().translate(0,0,200));

        //shader = new DefaultShader(new Renderable());
        shaderProgram = new ShaderProgram(vertexShader, fragmentShader);
	}

	@Override
	public void resize(int width, int height) {
        cam.viewportWidth = 1000f;
        cam.viewportHeight = 1000f * height/width;
        cam.update();
	}


    float progress = 0;
    float total = 10;
    int direction = 1;

    @Override
	public void render () {

		float deltaTime = Gdx.graphics.getDeltaTime();

//		float speedPerSecond = 500f;
//		//position = position + (speedPerSecond * deltaTime);
//
//		sprite.translate(speedPerSecond * deltaTime, 0);

		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		//batch.draw(img, 0, 0);

        for(MySprite ms : spriteIcons) {
            ms._sprite.draw(batch);
            glyphLayout.setText(font12, ms._name, Color.BLUE, 100f, Align.center, true);
            //font12.draw((batch, ms._name, ms._sprite.getX(), ms._sprite.getY());
            font12.draw(batch, glyphLayout, ms._sprite.getX(), ms._sprite.getY());
        }

		batch.end();

        Gdx.gl20.glEnable(GL20.GL_TEXTURE_2D);
        Gdx.gl20.glEnable(GL20.GL_BLEND);
        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        //_mapm.logd(TAG, "progress="+progress);
        progress += direction*deltaTime;
        if (progress>=total) {
            direction = -direction;
            progress=total;
        }
        else if (progress<=0) {
            direction = -direction;
            progress=0;
        }
        float amount = progress/total;
        //_mapm.logd(TAG, "amount=" + amount);
        float lerp = MathUtils.lerp(1, 500, amount);
        //_mapm.logd(TAG, "lerp=" + lerp);
        Matrix4 m = new Matrix4().translate(0,0,lerp);
        squareMesh.transform(m);


        spriteIcons.get(0)._texture.bind();
        shaderProgram.begin();
        shaderProgram.setUniformMatrix("u_worldView", batch.getProjectionMatrix());
        shaderProgram.setUniformi("u_texture", 0);
        squareMesh.render(shaderProgram, GL20.GL_TRIANGLE_STRIP, 0, 4);
        shaderProgram.end();

        squareMesh.transform(m.inv());
 	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}


	/*********************************************** GUI ***/

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		_mapm.logd(TAG, "touchDown");
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		_mapm.logd(TAG, "tap");
        ResetCamera(cam);
        cam.update();
        return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		_mapm.logd(TAG, "longPress");
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		_mapm.logd(TAG, "fling");
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
        //_mapm.logd(TAG, "pan x:" + x + " y:" + y + " deltaX:" + deltaX + " deltaY:"+deltaY);
        cam.translate(-deltaX,-deltaY, 0);
		cam.update();
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		_mapm.logd(TAG, "panStop");
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		_mapm.logd(TAG, "zoom");
		cam.translate(0,0,0.5f);
		cam.update();
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		_mapm.logd(TAG, "pinch");
		cam.translate(0,0,0.5f);
		cam.update();
		return false;
	}

}
