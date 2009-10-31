package net.vidageek.mirror.provider.performance;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import junit.framework.Assert;
import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.fixtures.FieldFixture;
import net.vidageek.mirror.fixtures.MethodFixture;
import net.vidageek.mirror.provider.java.DefaultMirrorReflectionProvider;
import net.vidageek.mirror.provider.sun15.Sun15ReflectionProvider;

import org.junit.Before;
import org.junit.Test;

/**
 * @author jonasabreu
 * 
 */
final public class TestSunInternalReflectionProviderIsFasterThanTraditionalReflection {

    private static final int BATCH_SIZE = 1000000;
    private Mirror sunMirror;
    private Mirror defaultMirror;

    @Before
    public void setup() {
        sunMirror = new Mirror(new Sun15ReflectionProvider());
        defaultMirror = new Mirror(new DefaultMirrorReflectionProvider());
    }

    @Test
    public void testThatMethodInvokingIsFasterUsingSunInternalClasses() {
        Method method = defaultMirror.on(MethodFixture.class).reflect().method("methodWithNoArgs").withoutArgs();
        MethodFixture fixture = new MethodFixture();

        long begin = System.currentTimeMillis();

        for (int i = 0; i < BATCH_SIZE; i++) {
            defaultMirror.on(fixture).invoke().method(method).withoutArgs();
        }

        long defaultGap = System.currentTimeMillis() - begin;

        begin = System.currentTimeMillis();

        for (int i = 0; i < BATCH_SIZE; i++) {
            sunMirror.on(fixture).invoke().method(method).withoutArgs();
        }

        long sunGap = System.currentTimeMillis() - begin;
        System.out.println("Sun method invocation: " + sunGap);
        System.out.println("Default method invocation: " + defaultGap);
        Assert.assertTrue("Sun implementation took " + sunGap + " millisseconds and default implemetation took "
                + defaultGap + " millisseconds.", sunGap < defaultGap);
    }

    @Test
    public void testThatFieldGettingIsFasterUsingSunInternalClasses() {
        Field field = defaultMirror.on(FieldFixture.class).reflect().field("field");
        FieldFixture fixture = new FieldFixture(10);
        long begin = System.currentTimeMillis();

        for (int i = 0; i < BATCH_SIZE; i++) {
            defaultMirror.on(fixture).get().field(field);
        }

        long defaultGap = System.currentTimeMillis() - begin;

        begin = System.currentTimeMillis();

        for (int i = 0; i < BATCH_SIZE; i++) {
            sunMirror.on(fixture).get().field(field);
        }

        long sunGap = System.currentTimeMillis() - begin;
        System.out.println("Sun field getting: " + sunGap);
        System.out.println("Default field getting: " + defaultGap);
        Assert.assertTrue("Sun implementation took " + sunGap + " millisseconds and default implemetation took "
                + defaultGap + " millisseconds.", sunGap < defaultGap);
    }

    @Test
    public void testThatFieldSettingIsFasterUsingSunInternalClasses() {
        Field field = defaultMirror.on(FieldFixture.class).reflect().field("field");
        FieldFixture fixture = new FieldFixture(10);
        long begin = System.currentTimeMillis();

        for (int i = 0; i < BATCH_SIZE; i++) {
            defaultMirror.on(fixture).set().field(field).withValue(Integer.MAX_VALUE);
        }

        long defaultGap = System.currentTimeMillis() - begin;

        begin = System.currentTimeMillis();

        for (int i = 0; i < BATCH_SIZE; i++) {
            sunMirror.on(fixture).set().field(field).withValue(Integer.MAX_VALUE);
        }

        long sunGap = System.currentTimeMillis() - begin;
        System.out.println("Sun field setting: " + sunGap);
        System.out.println("Default field setting: " + defaultGap);
        Assert.assertTrue("Sun implementation took " + sunGap + " millisseconds and default implemetation took "
                + defaultGap + " millisseconds.", sunGap < defaultGap);
    }
}
