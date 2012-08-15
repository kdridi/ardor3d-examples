package com.arykow.ardor3d.example;

import com.ardor3d.annotation.MainThread;
import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.framework.CanvasRenderer;
import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.framework.FrameHandler;
import com.ardor3d.framework.Scene;
import com.ardor3d.framework.Updater;
import com.ardor3d.framework.lwjgl.LwjglCanvas;
import com.ardor3d.framework.lwjgl.LwjglCanvasRenderer;
import com.ardor3d.image.Texture;
import com.ardor3d.image.util.AWTImageLoader;
import com.ardor3d.input.MouseManager;
import com.ardor3d.input.PhysicalLayer;
import com.ardor3d.input.lwjgl.LwjglControllerWrapper;
import com.ardor3d.input.lwjgl.LwjglKeyboardWrapper;
import com.ardor3d.input.lwjgl.LwjglMouseManager;
import com.ardor3d.input.lwjgl.LwjglMouseWrapper;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.light.PointLight;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.MathUtils;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.ContextCapabilities;
import com.ardor3d.renderer.ContextManager;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.TextureRendererFactory;
import com.ardor3d.renderer.TextureRendererProvider;
import com.ardor3d.renderer.lwjgl.LwjglTextureRendererProvider;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.LightState;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.MaterialState.ColorMaterial;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.renderer.state.WireframeState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.shape.Box;
import com.ardor3d.util.Constants;
import com.ardor3d.util.ContextGarbageCollector;
import com.ardor3d.util.GameTaskQueue;
import com.ardor3d.util.GameTaskQueueManager;
import com.ardor3d.util.ReadOnlyTimer;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.Timer;
import com.ardor3d.util.stat.StatCollector;

public class Example02 {

	public static abstract class Property<T> {
		private T property = null;

		public T get() {
			if (property == null) {
				property = create();
			}
			return property;
		}

		protected abstract T create();
	}

	public static void main(String[] args) {
		Example02 example02 = new Example02();
		new Thread(example02._runnable.get()).start();
	}

	private final Property<DisplaySettings> _displaySettings = new Property<DisplaySettings>() {
		public DisplaySettings create() {
			return new DisplaySettings(800, 600, 24, 0);
		}
	};

	private final Property<PhysicalLayer> _physicalLayer = new Property<PhysicalLayer>() {
		public PhysicalLayer create() {
			return new PhysicalLayer(new LwjglKeyboardWrapper(), new LwjglMouseWrapper(), new LwjglControllerWrapper(), _canvas.get());
		}
	};

	private final Property<MouseManager> _mouseManager = new Property<MouseManager>() {
		public MouseManager create() {
			return new LwjglMouseManager();
		}
	};

	private final Property<LwjglCanvas> _canvas = new Property<LwjglCanvas>() {
		private final Property<TextureRendererProvider> _textureRendererProvider = new Property<TextureRendererProvider>() {
			protected TextureRendererProvider create() {
				return new LwjglTextureRendererProvider();
			}
		};

		protected LwjglCanvas create() {
			final LwjglCanvasRenderer canvasRenderer = new LwjglCanvasRenderer(new Scene() {
				@MainThread
				public boolean renderUnto(Renderer renderer) {
					// TODO Auto-generated method stub
					return false;
				}

				public PickResults doPick(Ray3 pickRay) {
					// TODO Auto-generated method stub
					return null;
				}
			});
			TextureRendererFactory.INSTANCE.setProvider(_textureRendererProvider.get());
			return new LwjglCanvas(_displaySettings.get(), canvasRenderer);
		}
	};

	private final Property<Runnable> _runnable = new Property<Runnable>() {

		private final Property<FrameHandler> _frameHandler = new Property<FrameHandler>() {
			private final Property<Timer> _timer = new Property<Timer>() {
				protected Timer create() {
					return new Timer();
				}
			};

			protected FrameHandler create() {
				FrameHandler frameHandler = new FrameHandler(_timer.get());
				frameHandler.addUpdater(new Updater() {
					private final Property<Node> _root = new Property<Node>() {
						protected Node create() {
							final Node node = new Node();
							node.getSceneHints().setRenderBucketType(RenderBucketType.Opaque);
							node.updateGeometricState(0);

							final ZBufferState zBufferState = new ZBufferState();
							zBufferState.setEnabled(true);
							zBufferState.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
							node.setRenderState(zBufferState);

							final PointLight pointLight = new PointLight();
							pointLight.setDiffuse(new ColorRGBA(0.75f, 0.75f, 0.75f, 0.75f));
							pointLight.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
							pointLight.setLocation(new Vector3(100, 100, 100));
							pointLight.setEnabled(true);

							final LightState lightState = new LightState();
							lightState.setEnabled(true);
							lightState.attach(pointLight);
							node.setRenderState(lightState);

							final WireframeState wireframeState = new WireframeState();
							wireframeState.setEnabled(false);
							node.setRenderState(wireframeState);

							node.attachChild(box.get());

							return node;
						}
					};

					@MainThread
					public void init() {
						final ContextCapabilities caps = ContextManager.getCurrentContext().getCapabilities();
						// registerInputTriggers();

						AWTImageLoader.registerLoader();

					}

					private final Property<Mesh> box = new Property<Mesh>() {
						protected Mesh create() {
							Box box = new Box("Box", new Vector3(0, 0, 0), 5, 5, 5);
							box.setModelBound(new BoundingBox());
							box.setTranslation(new Vector3(0, 0, -15));
							box.setRandomColors();

							final TextureState ts = new TextureState();
							ts.setTexture(TextureManager.load("images/ardor3d_white_256.jpg", Texture.MinificationFilter.Trilinear, true));
							box.setRenderState(ts);

							final MaterialState ms = new MaterialState();
							ms.setColorMaterial(ColorMaterial.Diffuse);
							box.setRenderState(ms);

							return box;
						}
					};

					/** Rotation matrix for the spinning box. */
					private final Property<Matrix3> rotate = new Property<Matrix3>() {
						protected Matrix3 create() {
							Matrix3 rotate = new Matrix3();
							return rotate;
						}
					};

					/** Axis to rotate the box around. */
					private final Vector3 axis = new Vector3(1, 1, 0.5f).normalizeLocal();

					/** Angle of rotation for the box. */
					private double angle = 0;

					@MainThread
					public void update(ReadOnlyTimer timer) {

						if (_canvas.get().isClosing()) {
							_exit = true;
						}

						/** update stats, if enabled. */
						if (Constants.stats) {
							StatCollector.update();
						}

						// updateLogicalLayer(timer);

						GameTaskQueueManager.getManager(_canvas.get().getCanvasRenderer().getRenderContext()).getQueue(GameTaskQueue.UPDATE).execute();
						{
							angle += timer.getTimePerFrame() * 50;
							angle %= 360;
							rotate.get().fromAngleNormalAxis(angle * MathUtils.DEG_TO_RAD, axis);
							box.get().setRotation(rotate.get());
						}

						_root.get().updateGeometricState(timer.getTimePerFrame(), true);
					}
				});
				frameHandler.addCanvas(_canvas.get());

				return frameHandler;
			}
		};

		protected volatile boolean _exit = false;

		protected Runnable create() {
			return new Runnable() {
				public boolean QUIT_VM_ON_EXIT = true;

				@Override
				public void run() {
					try {
						_frameHandler.get().init();
						while (!_exit) {
							_frameHandler.get().updateFrame();
							Thread.yield();
						}
						// grab the graphics context so cleanup will work out.
						final CanvasRenderer cr = _canvas.get().getCanvasRenderer();
						cr.makeCurrentContext();
						quit(_canvas.get().getCanvasRenderer().getRenderer());
						cr.releaseCurrentContext();
						if (QUIT_VM_ON_EXIT) {
							System.exit(0);
						}
					} catch (final Throwable t) {
						System.err.println("Throwable caught in MainThread - exiting");
						t.printStackTrace(System.err);
					}

				}

				protected void quit(final Renderer renderer) {
					ContextGarbageCollector.doFinalCleanup(renderer);
					_canvas.get().close();
				}
			};
		}

	};

}
