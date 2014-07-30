package org.bcps.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
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
              if (INSTRUMENTATION_DEBUG)
                System.out.println("Visiting method " + m.getName());
              if (config.getMethodRegex().matcher(m.getName()).matches())
              {
                if (INSTRUMENTATION_DEBUG)
                  System.out.println("Matching method");
                // instrumentation
                currentMethod = m;
                if (config.getBeforeMethod() != null && config.getBeforeMethod().length() > 0)
                  m.insertBefore(config.getBeforeMethod().replace("$METHOD_NAME", m.getName()));
                if (config.getAfterMethod() != null && config.getAfterMethod().length() > 0)
                  m.insertAfter(config.getAfterMethod().replace("$METHOD_NAME", m.getName()));
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
          if (INSTRUMENTATION_DEBUG)
          {
            System.err.println("Skipping " + currentClass + "." + currentMethod.getName() + " due to an error:");
            ex.printStackTrace(System.err);
          }
        }

    // not instrumented, return original bytecode
    return byteCode;
  }
}
