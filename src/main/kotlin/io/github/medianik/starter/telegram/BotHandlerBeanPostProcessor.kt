package io.github.medianik.starter.telegram

import io.github.medianik.starter.telegram.annotation.BotHandler
import org.springframework.aop.framework.Advised
import org.springframework.aop.support.AopUtils
import org.springframework.beans.BeansException
import org.springframework.beans.FatalBeanException
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.core.annotation.AnnotationUtils

class BotHandlerBeanPostProcessor : BeanPostProcessor {

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        val currentBean = getTargetObject(bean)

        if (hasAnnotation(currentBean, BotHandler::class.java)) {
            process(currentBean)
        }
        return bean
    }

    private fun process(bean: Any){

    }

    private fun hasAnnotation(currentBean: Any, annotationClass: Class<out Annotation>) =
        AnnotationUtils.findAnnotation(currentBean.javaClass, annotationClass) != null

    @Throws(BeansException::class)
    private fun getTargetObject(proxy: Any): Any {
        return if (AopUtils.isJdkDynamicProxy(proxy)) {
            try {
                (proxy as Advised).targetSource.target!!
            } catch (e: Exception) {
                throw FatalBeanException("Error getting target of JDK proxy", e)
            }
        } else proxy
    }
}