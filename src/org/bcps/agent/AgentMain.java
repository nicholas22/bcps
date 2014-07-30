package org.bcps.agent;

import java.io.File;
import java.lang.instrument.Instrumentation;
import org.bcps.config.InstrumentationClassConfig;
import org.bcps.config.InstrumentationClassConfigParser;

/**
 * Main agent entry point, to use specify -javaagent:/path/to/bcps.jar=[SAMPLE|INSTRUMENT] as a JVM argument. The agent library JAR must
 * contain a MANIFEST.MF file, which includes this line:
 * 
 * <pre>
 * Premain-Class: org.bcps.agent.AgentMain
 * </pre>
 * 
 * A config file is expected for any of the two (sampling & instrumentation) modes. Examples of what the configs look like can be found
 * under this project's /config folder.
 */
public class AgentMain
{
  private static final String INSTRUMENT = "instrument";
  private static final String SAMPLE = "sample";

  private static final String INSTRUMENTATION_CONFIG_PROP = "bcps.instrumentation.config";
  private static final String SAMPLING_CONFIG_PROP = "bcps.instrumentation.config";

  public static void premain(final String agentArgs, final Instrumentation inst)
      throws Exception
  {
    if (INSTRUMENT.equals(agentArgs))
      premainInstrument(inst);
    else if (SAMPLE.equals(agentArgs))
      premainSample();
    else
      // caller should have configured a mode
      throw new RuntimeException("Unrecognised profiling type, you may use " + INSTRUMENT + " or " + SAMPLE + " as an agent argument");
  }

  private static void premainInstrument(final Instrumentation inst)
      throws Exception
  {
    // locate instrumentation config file
    File configFile = findConfig(INSTRUMENTATION_CONFIG_PROP, "instrumentation");
    // parse config to determine which class/methods to instrument
    Iterable<InstrumentationClassConfig> classConfigs = InstrumentationClassConfigParser.parse(configFile);
    // register class transformer
    inst.addTransformer(new AopClassTransformer(classConfigs));
  }

  private static void premainSample()
      throws Exception
  {
    // locate sampling config file
    File configFile = findConfig(SAMPLING_CONFIG_PROP, "sampling");
  }

  private static File findConfig(final String containingSystemProperty, final String configType)
  {
    String configAbsPath = System.getProperty(containingSystemProperty);
    if (configAbsPath == null)
      throw new IllegalArgumentException("Expected an " + configType + " config file set as system property: " + containingSystemProperty);
    File configFile = new File(configAbsPath);
    if (!configFile.exists())
      throw new IllegalArgumentException("The " + configType + " config file not found: " + configFile);
    return configFile;
  }

}
