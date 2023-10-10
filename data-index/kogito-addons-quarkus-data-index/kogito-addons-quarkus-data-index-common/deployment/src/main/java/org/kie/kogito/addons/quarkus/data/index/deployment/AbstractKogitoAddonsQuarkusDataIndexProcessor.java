package org.kie.kogito.addons.quarkus.data.index.deployment;

import org.jboss.jandex.DotName;
import org.jboss.jandex.Type;
import org.kie.kogito.index.addon.vertx.VertxGraphiQLSetup;
import org.kie.kogito.index.model.Node;
import org.kie.kogito.index.model.ProcessDefinition;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.quarkus.addons.common.deployment.KogitoCapability;
import org.kie.kogito.quarkus.addons.common.deployment.OneOfCapabilityKogitoAddOnProcessor;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.processor.DotNames;
import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveHierarchyBuildItem;
import io.quarkus.deployment.pkg.steps.NativeOrNativeSourcesBuild;

public abstract class AbstractKogitoAddonsQuarkusDataIndexProcessor extends OneOfCapabilityKogitoAddOnProcessor {

    AbstractKogitoAddonsQuarkusDataIndexProcessor() {
        super(KogitoCapability.SERVERLESS_WORKFLOW, KogitoCapability.PROCESSES);
    }

    @BuildStep(onlyIf = IsDevelopment.class)
    public void processGraphiql(BuildProducer<AdditionalBeanBuildItem> additionalBean) {
        additionalBean.produce(AdditionalBeanBuildItem.builder().addBeanClass(VertxGraphiQLSetup.class).setUnremovable().setDefaultScope(DotNames.APPLICATION_SCOPED).build());
    }

    @BuildStep(onlyIf = NativeOrNativeSourcesBuild.class)
    public void nativeResources(BuildProducer<NativeImageResourceBuildItem> resource,
            BuildProducer<ReflectiveHierarchyBuildItem> reflectiveHierarchyClass) {
        resource.produce(new NativeImageResourceBuildItem("basic.schema.graphqls"));
        resource.produce(new NativeImageResourceBuildItem("io/vertx/ext/web/handler/graphiql/index.html"));
        reflectiveHierarchy(Node.class, reflectiveHierarchyClass);
        reflectiveHierarchy(ProcessDefinition.class, reflectiveHierarchyClass);
        reflectiveHierarchy(ProcessInstance.class, reflectiveHierarchyClass);
        reflectiveHierarchy(UserTaskInstance.class, reflectiveHierarchyClass);
    }

    protected void reflectiveHierarchy(Class<?> clazz, BuildProducer<ReflectiveHierarchyBuildItem> reflectiveHierarchyClass) {
        DotName dotName = DotName.createSimple(clazz.getName());
        Type type = Type.create(dotName, Type.Kind.CLASS);
        reflectiveHierarchyClass.produce(new ReflectiveHierarchyBuildItem.Builder().type(type).build());
    }

}
