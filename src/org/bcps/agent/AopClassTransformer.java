package org.bcps.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.concurrent.atomic.AtomicInteger;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.bcps.config.InstrumentationClassConfig;
import org.bcps.helpers.JavaPropsHelper;

/**
 * A class file transformer that weaves timing logic around loaded classes. Checks for matching classes to weave bytecode around using the
 * regex passed to it via the constructor.
 */
public class AopClassTransformer
    implements ClassFileTransformer
{
  private final Iterable<InstrumentationClassConfig> configs;
  private final String INSTRUMENTATION_DEBUG_PROP = "bcps.instrumentation.debug";
  private final boolean INSTRUMENTATION_DEBUG = Boolean.parseBoolean(JavaPropsHelper.getString(INSTRUMENTATION_DEBUG_PROP, "false"));
  private final String INSTRUMENTATION_VERBOSE_ERROR_PROP = "bcps.instrumentation.verbose.error";
  private final boolean INSTRUMENTATION_VERBOSE_ERROR = Boolean.parseBoolean(JavaPropsHelper.getString(INSTRUMENTATION_VERBOSE_ERROR_PROP,
      "false"));
  private final String INSTRUMENTATION_SUPRESS_ERRORS_PROP = "bcps.instrumentation.supress.errors";
  private final boolean INSTRUMENTATION_SUPRESS_ERRORS = Boolean.parseBoolean(JavaPropsHelper.getString(
      INSTRUMENTATION_SUPRESS_ERRORS_PROP, "false"));

  public AopClassTransformer(final Iterable<InstrumentationClassConfig> configs)
  {
    if (configs == null)
      throw new NullPointerException("configs");
    this.configs = configs;
    if (INSTRUMENTATION_DEBUG)
      System.out.println("Using configurations: " + configs);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined,
                          final ProtectionDomain protectionDomain, final byte[] classfileBuffer)
      throws IllegalClassFormatException
  {
    // used for enriching error reporting
    String currentMethodName = null;
    AtomicInteger methodBlocksUpdated = new AtomicInteger();

    try
    {
      // convert to dot FQCN notation
      String currentClass = className.replace('/', '.');
      if (INSTRUMENTATION_DEBUG)
        System.out.println("Visiting class " + currentClass);

      // this may fail if javassist is not in the classpath of the VM agent
      ClassPool cp = ClassPool.getDefault();

      for (InstrumentationClassConfig config : configs)
        // iterate matching classes
        if (config.getClassRegex().matcher(currentClass).matches())
        {
          if (INSTRUMENTATION_DEBUG)
            System.out.println("Matching class");
          CtClass cc = cp.get(currentClass);
          if (!cc.isInterface()) // skip interfaces
            // iterate matching class' methods
            for (CtMethod currentMethod : cc.getDeclaredMethods())
            {
              currentMethodName = currentMethod.getName();
              if (INSTRUMENTATION_DEBUG)
                System.out.println("Visiting method " + currentMethodName);
              if (config.getMethodRegex().matcher(currentMethodName).matches())
              {
                if (INSTRUMENTATION_DEBUG)
                  System.out.println("Matching method");
                // instrumentation & post-process user args
                if (config.getBeforeMethod() != null && config.getBeforeMethod().length() > 0)
                  currentMethod.insertBefore(postprocess(config.getBeforeMethod(), currentMethod, methodBlocksUpdated));
                if (config.getAfterMethod() != null && config.getAfterMethod().length() > 0)
                  currentMethod.insertAfter(postprocess(config.getAfterMethod(), currentMethod, methodBlocksUpdated));
              }
              // reset in preparation of next method
              currentMethodName = null;
            }

          try
          {
            // use new bytecode if any changes applied
            if (methodBlocksUpdated.get() > 0)
              return cc.toBytecode();
          }
          finally
          {
            // detach in all cases
            cc.detach();
          }
        }
    }
    catch(Throwable t)
    {
      String methodName = (currentMethodName != null ? ("." + currentMethodName) : "");
      errorReport("Skipping " + className + methodName + " transformation due to an error: ", t);
    }

    // not instrumented, return original bytecode
    return classfileBuffer;
  }

  /**
   * Replaces string variables in code with actual values such as class/method names, arguments, etc.
   */
  private String postprocess(final String originalCode, final CtMethod currentMethod, final AtomicInteger methodBlocksUpdated)
  {
    try
    {
      String className = currentMethod.getDeclaringClass().getSimpleName();
      // class and method name replacement
      String postProcessedCode = originalCode.replace("$CLASS_NAME", className).replace("$METHOD_NAME", currentMethod.getName());

      // argument replacement
      String arg1 = "\"\"";
      String arg2 = arg1;
      String arg3 = arg2;
      String arg4 = arg3;
      String arg5 = arg4;
      CtClass[] argTypes = currentMethod.getParameterTypes();
      arg1 = argTypes.length >= 1 ? "$1" : arg1;
      arg2 = argTypes.length >= 2 ? "$2" : arg2;
      arg3 = argTypes.length >= 3 ? "$3" : arg3;
      arg4 = argTypes.length >= 4 ? "$4" : arg4;
      arg5 = argTypes.length >= 5 ? "$5" : arg5;
      postProcessedCode = postProcessedCode.replace("$ARG1", arg1).replace("$ARG2", arg2).replace("$ARG3", arg3).replace("$ARG4", arg4)
          .replace("$ARG5", arg5);

      if (INSTRUMENTATION_DEBUG)
      {
        System.out.println("Before post-process: " + originalCode);
        System.out.println("After post-process: " + postProcessedCode);
      }

      // maintain instrumentation metrics
      methodBlocksUpdated.incrementAndGet();

      return postProcessedCode;
    }
    catch(Throwable t)
    {
      // return original code
      errorReport("Skipping " + currentMethod.getDeclaringClass().getSimpleName() + "." + currentMethod.getName()
          + " post-processing due to an error: ", t);
      return originalCode;
    }
  }

  private void errorReport(final String msg, final Throwable cause)
  {
    if (!INSTRUMENTATION_SUPRESS_ERRORS)
    {
      System.err.print(msg);
      if (INSTRUMENTATION_VERBOSE_ERROR)
        cause.printStackTrace(System.err);
      else
        System.err.println(cause.getMessage());
    }
  }
}
