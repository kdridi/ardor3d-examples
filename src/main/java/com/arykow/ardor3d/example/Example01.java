package com.arykow.ardor3d.example;

import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.example.basic.BoxExample;
import com.ardor3d.framework.Canvas;
import com.ardor3d.image.Texture;
import com.ardor3d.input.Key;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.KeyPressedCondition;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.math.MathUtils;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.renderer.state.MaterialState.ColorMaterial;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.controller.SpatialController;
import com.ardor3d.scenegraph.shape.Box;
import com.ardor3d.util.TextureManager;

public class Example01 extends BoxExample {

	public static void main(final String[] args) {
		start(BoxExample.class);
	}

	@Override
	public PickResults doPick(Ray3 pickRay) {
		final PickResults result = super.doPick(pickRay);
		System.out.println(result);
		return result;
	}

	private int materialIndex = 0;

	@Override
	protected void registerInputTriggers() {
		super.registerInputTriggers();

		_logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.TAB), new TriggerAction() {
			public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
				if (++materialIndex == ColorMaterial.values().length) {
					materialIndex = 0;
				}
				System.out.println(ColorMaterial.values()[materialIndex]);
			}
		}));
	}

	@Override
	protected void initExample() {
		_canvas.setTitle("Box Example");
		_root.attachChild(new Box("Box", new Vector3(0, 0, 0), 5, 5, 5) {
			private final Matrix3 rotate = new Matrix3();
			private double angle = 0;
			private final Vector3 axis = new Vector3(0, 1, 0).normalizeLocal();
			private final MaterialState materialState = new MaterialState();

			{

				final TextureState ts = new TextureState();
				ts.setTexture(TextureManager.load("images/ardor3d_white_256.jpg", Texture.MinificationFilter.Trilinear, true));
				setRenderState(ts);

				setRenderState(materialState);

				setModelBound(new BoundingBox());
				setTranslation(new Vector3(0, 0, -15));
				setRandomColors();
				addController(new SpatialController<Spatial>() {
					public void update(double time, Spatial caller) {
						angle += time * 50;
						angle %= 360;

						materialState.setColorMaterial(ColorMaterial.values()[materialIndex]);
						rotate.fromAngleNormalAxis(angle * MathUtils.DEG_TO_RAD, axis);
						setRotation(rotate);
					}
				});
			}
		});
	}
}
