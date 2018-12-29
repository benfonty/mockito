package com.example.demo;

import com.example.demo.beans.BeanOne;
import com.example.demo.services.ServiceOne;
import com.example.demo.services.ServiceTwo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


public class DemoApplicationTests {

    @Mock
    ServiceOne serviceOne;

    @Mock
    ServiceTwo serviceTwo;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testOne() throws Exception {
        serviceOne.functionOne("toto", true);
    }

    @Test
    public void testTwo() throws Exception {
        doThrow(IOException.class).when(serviceOne).functionOne(eq("titi"), anyBoolean());
        serviceOne.functionOne("toto", true);

        verify(serviceOne, times(1)).functionOne(anyString(), eq(Boolean.TRUE));
    }

    @Test
    public void testCaptured() throws Exception {
        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        doNothing().when(serviceOne).functionOne(valueCapture.capture(), anyBoolean());
        serviceOne.functionOne("toto", true);
        assertThat(valueCapture.getValue()).isEqualTo("toto");
    }

    @Test(expected = IOException.class)
    public void testThree() throws Exception {
        doThrow(IOException.class).when(serviceOne).functionOne(eq("titi"), anyBoolean());
        serviceOne.functionOne("titi", true);
    }

    @Test
    public void testFour() throws Exception {
        final BeanOne bean1 = BeanOne.builder().toto("toto").build();
        final BeanOne bean2 = BeanOne.builder().toto("titi").build();
        when(serviceTwo.functionOne(bean1)).thenReturn(true);
        when(serviceTwo.functionOne(bean2)).thenReturn(false);

        assertThat(serviceTwo.functionOne(bean1)).isTrue();
        assertThat(serviceTwo.functionOne(bean2)).isFalse();

        verify(serviceTwo, times(2)).functionOne(any(BeanOne.class));
    }

    @Test
    public void testChangeArgumentOne() throws IOException {
        final BeanOne bean1 = BeanOne.builder().toto("toto").build();
        doAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    ((BeanOne) args[0]).setToto("yesitworks");
                    return true;
                }

        ).when(serviceTwo).functionOne(any(BeanOne.class));

        assertThat(serviceTwo.functionOne(bean1)).isTrue();
        assertThat(bean1.getToto()).isEqualTo("yesitworks");
    }

    @Test
    public void testChangeArgumentTwo() throws IOException {
        final BeanOne bean1 = BeanOne.builder().toto("toto").build();
        doAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    ((BeanOne) args[0]).setToto("yesitworkss");
                    return null;
                }

        ).when(serviceTwo).functionTwo(any(BeanOne.class));

        serviceTwo.functionTwo(bean1);
        assertThat(bean1.getToto()).isEqualTo("yesitworkss");
    }

}

