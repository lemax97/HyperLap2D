package games.rednblack.editor.view.ui.dialog;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.puremvc.patterns.mediator.SimpleMediator;
import com.puremvc.patterns.observer.Notification;
import games.rednblack.editor.HyperLap2DFacade;
import games.rednblack.editor.renderer.components.PolygonComponent;
import games.rednblack.editor.renderer.components.TextureRegionComponent;
import games.rednblack.editor.utils.poly.PolygonUtils;
import games.rednblack.editor.utils.poly.tracer.Tracer;
import games.rednblack.editor.view.stage.Sandbox;
import games.rednblack.editor.view.stage.UIStage;
import games.rednblack.h2d.common.MsgAPI;

import java.util.stream.Stream;

public class AutoTraceDialogMediator extends SimpleMediator<AutoTraceDialog> {

    private static final String TAG = AutoTraceDialogMediator.class.getCanonicalName();
    private static final String NAME = TAG;

    private Entity entity;

    public AutoTraceDialogMediator() {
        super(NAME, new AutoTraceDialog());
    }

    @Override
    public void onRegister() {
        super.onRegister();
        facade = HyperLap2DFacade.getInstance();
    }

    @Override
    public String[] listNotificationInterests() {
        return new String[]{
                AutoTraceDialog.OPEN_DIALOG,
                AutoTraceDialog.AUTO_TRACE_BUTTON_CLICKED
        };
    }

    @Override
    public void handleNotification(Notification notification) {
        super.handleNotification(notification);
        Sandbox sandbox = Sandbox.getInstance();
        UIStage uiStage = sandbox.getUIStage();

        switch (notification.getName()) {
            case AutoTraceDialog.OPEN_DIALOG:
                entity = notification.getBody();
                viewComponent.show(uiStage);
                break;
            case AutoTraceDialog.AUTO_TRACE_BUTTON_CLICKED:
                addAutoTraceMesh();
                break;
        }
    }

    private void addAutoTraceMesh() {
        PolygonComponent polygonComponent = entity.getComponent(PolygonComponent.class);

        if (polygonComponent != null) {
            TextureRegionComponent textureRegionComponent = entity.getComponent(TextureRegionComponent.class);

            if (!textureRegionComponent.regionName.equals("") && textureRegionComponent.region != null) {

                polygonComponent.vertices = Tracer.trace(textureRegionComponent.region, viewComponent.getHullTolerance(),
                        viewComponent.getAlphaTolerance(), viewComponent.isMultiPartDetection(),
                        viewComponent.isHoleDetection());

                if (polygonComponent.vertices != null) {
                    Vector2[] points = Stream.of(polygonComponent.vertices)
                            .flatMap(Stream::of)
                            .toArray(Vector2[]::new);
                    polygonComponent.vertices = PolygonUtils.polygonize(points);

                    HyperLap2DFacade.getInstance().sendNotification(MsgAPI.ITEM_DATA_UPDATED, entity);
                }
            }
        }
    }
}
