package org.bcps.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.MethodInfo;
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
    byte[] byteCode = classfileBuffer;

    String currentClass = className.replace('/', '.');
    if (INSTRUMENTATION_DEBUG)
      System.out.println("Visiting class " + currentClass);
    ClassPool cp = ClassPool.getDefault();
    CtMethod currentMethod = null;

    for (InstrumentationClassConfig config : configs)
      // iterate matching classes
      if (config.getClassRegex().matcher(currentClass).matches())
        try
        {
          if (INSTRUMENTATION_DEBUG)
            System.out.println("Matching class");
          CtClass cc = cp.get(currentClass);
          if (!cc.isInterface()) // skip interfaces
            // iterate matching methods
            for (CtMethod m : cc.getDeclaredMethods())
            {
              currentMethod = m;
              if (INSTRUMENTATION_DEBUG)
                System.out.println("Visiting method " + m.getName());
              if (config.getMethodRegex().matcher(m.getName()).matches())
              {
                if (INSTRUMENTATION_DEBUG)
                  System.out.println("Matching method");
                MethodInfo methodInfo = m.getMethodInfo();
                // instrumentation & post-process user args
                if (config.getBeforeMethod() != null && config.getBeforeMethod().length() > 0)
                  m.insertBefore(postprocess(config.getBeforeMethod(), currentMethod));
                if (config.getAfterMethod() != null && config.getAfterMethod().length() > 0)
                  m.insertAfter(postprocess(config.getAfterMethod(), currentMethod));
              }
            }
          // use new bytecode if any changes applied
          if (currentMethod != null)
            byteCode = cc.toBytecode();

          cc.detach();
          return byteCode;
        }
        catch(Exception ex)
        {
          System.err.println("Skipping " + currentClass + "." + currentMethod.getName() + " due to an error:");
          ex.printStackTrace(System.err);
        }

    // not instrumented, return original bytecode
    return byteCode;
  }

  /**
   * Replaces string variables in code with actual values such as class/method names, arguments, etc.
   */
  private String postprocess(final String code, final CtMethod currentMethod)
  {
    String className = currentMethod.getDeclaringClass().getSimpleName();
    // class and method name replacement
    String result = code.replace("$CLASS_NAME", className).replace("$METHOD_NAME", currentMethod.getName());
    // argument replacement
    String arg1 = "\"\"";
    String arg2 = arg1;
    String arg3 = arg2;
    String arg4 = arg3;
    try
    {
      CtClass[] argTypes = currentMethod.getParameterTypes();
      arg1 = argTypes.length >= 1 ? "$1" : arg1;
      arg2 = argTypes.length >= 2 ? "$2" : arg2;
      arg3 = argTypes.length >= 3 ? "$3" : arg3;
      arg4 = argTypes.length >= 4 ? "$4" : arg4;
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

    result = result.replace("$ARG1", arg1).replace("$ARG2", arg2).replace("$ARG3", arg3).replace("$ARG4", arg4);
    if (INSTRUMENTATION_DEBUG)
    {
      System.out.println("Before post-process: " + code);
      System.out.println("After post-process: " + result);
    }
    return result;
  }
}
