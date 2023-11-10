package dev.appkr.starter;

import static picocli.CommandLine.Model.UsageMessageSpec.SECTION_KEY_COMMAND_LIST;

import dev.appkr.starter.commands.GenerateCommand;
import dev.appkr.starter.commands.GenerateCommandForGradle;
import dev.appkr.starter.commands.GenerateCommandForJar;
import dev.appkr.starter.commands.GenerateCommandForNative;
import dev.appkr.starter.commands.PublishCommand;
import dev.appkr.starter.services.GlobalConstants;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Ansi.Style;
import picocli.CommandLine.Help.ColorScheme;

@Command(
    name = "msastarter",
    version = "3.4.4",
    mixinStandardHelpOptions = true,
    description = "Command that generates a Spring-boot microservice skeleton",
    optionListHeading = "%nOptions:%n",
    commandListHeading = "%nCommands:%n",
    footerHeading = "%n",
    footer = "appkr<juwonkim@me.com>"
)
public class MsaStarter {

  final static ColorScheme colorScheme = new ColorScheme.Builder()
      .commands(Style.bold)       // combine multiple styles
      .options(Style.fg_yellow)   // yellow foreground color
      .parameters(Style.fg_yellow)
      .optionParams(Style.italic)
      .errors(Style.fg_red, Style.bold)
      .stackTraces(Style.italic)
      .applySystemProperties()
      .build();

  public static void main(String[] args) {
    printRunningMode();

    final MsaStarter command = new MsaStarter();
    final CommandLine cli = new CommandLine(command)
        .addSubcommand(createGenerateCommand())
        .addSubcommand(new PublishCommand())
        .setColorScheme(colorScheme);

    if (args.length == 0) {
      cli.getHelpSectionMap().put(SECTION_KEY_COMMAND_LIST, new CommandListRenderer());
      cli.usage(System.out);
    } else {
      final int exitCode = cli.execute(args);
      System.exit(exitCode);
    }
  }

  private static void printRunningMode() {
    String runningMode = "GRADLE";
    if (isRunningInJar()) {
      runningMode = "JAR";
    }
    if (isRunningInNative()) {
      runningMode = "NATIVE";
    }

    System.out.println(String.format(
        CommandLine.Help.Ansi.AUTO.string("The application is running in @|bold,fg(red) %s|@ mode!!! %n"), runningMode));
  }

  static GenerateCommand createGenerateCommand() {
    if (isRunningInNative()) {
      return new GenerateCommandForNative();
    }

    if (isRunningInJar()) {
      return new GenerateCommandForJar();
    }

    return new GenerateCommandForGradle();
  }

  static boolean isRunningInJar() {
    // Reference: https://mkyong.com/java/java-read-a-file-from-resources-folder/
    try {
      final String fqcn = MsaStarter.class.getName().replace(".", GlobalConstants.DIR_SEPARATOR);
      final String classJar = MsaStarter.class.getResource(GlobalConstants.DIR_SEPARATOR + fqcn + ".class").toString();
      return classJar.startsWith("jar:");
    } catch (Exception e) {
      return false;
    }
  }

  static boolean isRunningInNative() {
    // Reference: https://stackoverflow.com/a/50291759
    // in Gradle: {java.runtime.name=OpenJDK Runtime Environment, sun.boot.library.path=/Library/Java/JavaVirtualMachines/amazon-corretto-8.jdk/Contents/Home/jre/lib, java.vm.version=25.342-b07, gopherProxySet=false, java.vm.vendor=Amazon.com Inc., java.vendor.url=https://aws.amazon.com/corretto/, path.separator=:, java.vm.name=OpenJDK 64-Bit Server VM, file.encoding.pkg=sun.io, sun.java.launcher=SUN_STANDARD, user.country=KR, sun.os.patch.level=unknown, java.vm.specification.name=Java Virtual Machine Specification, user.dir=/Users/appkr/workspace/msa-starter, java.runtime.version=1.8.0_342-b07, java.awt.graphicsenv=sun.awt.CGraphicsEnvironment, java.endorsed.dirs=/Library/Java/JavaVirtualMachines/amazon-corretto-8.jdk/Contents/Home/jre/lib/endorsed, os.arch=x86_64, java.io.tmpdir=/var/folders/w3/p26pq0hs3_15szf3sdstvcl40000gn/T/, line.separator=java.vm.specification.vendor=Oracle Corporation, user.variant=, os.name=Mac OS X, sun.jnu.encoding=UTF-8, java.library.path=/Users/appkr/Library/Java/Extensions:/Library/Java/Extensions:/Network/Library/Java/Extensions:/System/Library/Java/Extensions:/usr/lib/java:., java.specification.name=Java Platform API Specification, java.class.version=52.0, sun.management.compiler=HotSpot 64-Bit Tiered Compilers, os.version=12.6, http.nonProxyHosts=local|*.local|169.254/16|*.169.254/16|127.0.0.1:21300|*.127.0.0.1:21300|localhost|*.localhost|lx.astxsvc.com|*.lx.astxsvc.com, user.home=/Users/appkr, user.timezone=, java.awt.printerjob=sun.lwawt.macosx.CPrinterJob, file.encoding=UTF-8, java.specification.version=1.8, java.class.path=/Users/appkr/workspace/msa-starter/build/classes/java/main:/Users/appkr/workspace/msa-starter/build/resources/main:/Users/appkr/.gradle/caches/modules-2/files-2.1/info.picocli/picocli/4.6.3/18177f4c3d65cc94e6d4039775ec5aed8089f8d0/picocli-4.6.3.jar:/Users/appkr/.gradle/caches/modules-2/files-2.1/com.github.spullara.mustache.java/compiler/0.9.10/6111ae24e3be9ecbd75f5fe908583fc14b4f0174/compiler-0.9.10.jar:/Users/appkr/.gradle/caches/modules-2/files-2.1/org.eclipse.jgit/org.eclipse.jgit/5.13.1.202206130422-r/841d1ae74e4bc77ac7d4b106f15d0468dc7ac7f2/org.eclipse.jgit-5.13.1.202206130422-r.jar:/Users/appkr/.gradle/caches/modules-2/files-2.1/ch.qos.logback/logback-classic/1.2.9/7d495522b08a9a66084bf417e70eedf95ef706bc/logback-classic-1.2.9.jar:/Users/appkr/.gradle/caches/modules-2/files-2.1/com.googlecode.javaewah/JavaEWAH/1.1.13/32cd724a42dc73f99ca08453d11a4bb83e0034c7/JavaEWAH-1.1.13.jar:/Users/appkr/.gradle/caches/modules-2/files-2.1/org.slf4j/slf4j-api/1.7.32/cdcff33940d9f2de763bc41ea05a0be5941176c3/slf4j-api-1.7.32.jar:/Users/appkr/.gradle/caches/modules-2/files-2.1/ch.qos.logback/logback-core/1.2.9/cdaca0cf922c5791a8efa0063ec714ca974affe3/logback-core-1.2.9.jar, user.name=appkr, java.vm.specification.version=1.8, sun.java.command=dev.appkr.starter.MsaStarter generate, java.home=/Library/Java/JavaVirtualMachines/amazon-corretto-8.jdk/Contents/Home/jre, sun.arch.data.model=64, user.language=ko, java.specification.vendor=Oracle Corporation, awt.toolkit=sun.lwawt.macosx.LWCToolkit, java.vm.info=mixed mode, java.version=1.8.0_342, java.ext.dirs=/Users/appkr/Library/Java/Extensions:/Library/Java/JavaVirtualMachines/amazon-corretto-8.jdk/Contents/Home/jre/lib/ext:/Library/Java/Extensions:/Network/Library/Java/Extensions:/System/Library/Java/Extensions:/usr/lib/java, sun.boot.class.path=/Library/Java/JavaVirtualMachines/amazon-corretto-8.jdk/Contents/Home/jre/lib/resources.jar:/Library/Java/JavaVirtualMachines/amazon-corretto-8.jdk/Contents/Home/jre/lib/rt.jar:/Library/Java/JavaVirtualMachines/amazon-corretto-8.jdk/Contents/Home/jre/lib/sunrsasign.jar:/Library/Java/JavaVirtualMachines/amazon-corretto-8.jdk/Contents/Home/jre/lib/jsse.jar:/Library/Java/JavaVirtualMachines/amazon-corretto-8.jdk/Contents/Home/jre/lib/jce.jar:/Library/Java/JavaVirtualMachines/amazon-corretto-8.jdk/Contents/Home/jre/lib/charsets.jar:/Library/Java/JavaVirtualMachines/amazon-corretto-8.jdk/Contents/Home/jre/lib/jfr.jar:/Library/Java/JavaVirtualMachines/amazon-corretto-8.jdk/Contents/Home/jre/classes, java.vendor=Amazon.com Inc., file.separator=/, java.vendor.url.bug=https://github.com/corretto/corretto-8/issues/, sun.io.unicode.encoding=UnicodeBig, sun.cpu.endian=little, socksNonProxyHosts=local|*.local|169.254/16|*.169.254/16|127.0.0.1:21300|*.127.0.0.1:21300|localhost|*.localhost|lx.astxsvc.com|*.lx.astxsvc.com, ftp.nonProxyHosts=local|*.local|169.254/16|*.169.254/16|127.0.0.1:21300|*.127.0.0.1:21300|localhost|*.localhost|lx.astxsvc.com|*.lx.astxsvc.com, sun.cpu.isalist=}
    // in Jar   : {java.runtime.name=OpenJDK Runtime Environment, sun.boot.library.path=/Library/Java/JavaVirtualMachines/amazon-corretto-8.jdk/Contents/Home/jre/lib, java.vm.version=25.342-b07, gopherProxySet=false, java.vm.vendor=Amazon.com Inc., java.vendor.url=https://aws.amazon.com/corretto/, path.separator=:, java.vm.name=OpenJDK 64-Bit Server VM, file.encoding.pkg=sun.io, user.country=KR, sun.java.launcher=SUN_STANDARD, sun.os.patch.level=unknown, java.vm.specification.name=Java Virtual Machine Specification, user.dir=/Users/appkr/Desktop, java.runtime.version=1.8.0_342-b07, java.awt.graphicsenv=sun.awt.CGraphicsEnvironment, java.endorsed.dirs=/Library/Java/JavaVirtualMachines/amazon-corretto-8.jdk/Contents/Home/jre/lib/endorsed, os.arch=x86_64, java.io.tmpdir=/var/folders/w3/p26pq0hs3_15szf3sdstvcl40000gn/T/, line.separator=, java.vm.specification.vendor=Oracle Corporation, os.name=Mac OS X, sun.jnu.encoding=UTF-8, java.library.path=/Users/appkr/Library/Java/Extensions:/Library/Java/Extensions:/Network/Library/Java/Extensions:/System/Library/Java/Extensions:/usr/lib/java:., java.specification.name=Java Platform API Specification, java.class.version=52.0, sun.management.compiler=HotSpot 64-Bit Tiered Compilers, os.version=12.6, http.nonProxyHosts=local|*.local|169.254/16|*.169.254/16|127.0.0.1:21300|*.127.0.0.1:21300|localhost|*.localhost|lx.astxsvc.com|*.lx.astxsvc.com, user.home=/Users/appkr, user.timezone=, java.awt.printerjob=sun.lwawt.macosx.CPrinterJob, file.encoding=UTF-8, java.specification.version=1.8, java.class.path=msastarter-3.3.0-all.jar, user.name=appkr, java.vm.specification.version=1.8, sun.java.command=msastarter-3.3.0-all.jar generate, java.home=/Library/Java/JavaVirtualMachines/amazon-corretto-8.jdk/Contents/Home/jre, sun.arch.data.model=64, user.language=ko, java.specification.vendor=Oracle Corporation, user.language.format=ko-Kore, awt.toolkit=sun.lwawt.macosx.LWCToolkit, java.vm.info=mixed mode, java.version=1.8.0_342, java.ext.dirs=/Users/appkr/Library/Java/Extensions:/Library/Java/JavaVirtualMachines/amazon-corretto-8.jdk/Contents/Home/jre/lib/ext:/Library/Java/Extensions:/Network/Library/Java/Extensions:/System/Library/Java/Extensions:/usr/lib/java, sun.boot.class.path=/Library/Java/JavaVirtualMachines/amazon-corretto-8.jdk/Contents/Home/jre/lib/resources.jar:/Library/Java/JavaVirtualMachines/amazon-corretto-8.jdk/Contents/Home/jre/lib/rt.jar:/Library/Java/JavaVirtualMachines/amazon-corretto-8.jdk/Contents/Home/jre/lib/sunrsasign.jar:/Library/Java/JavaVirtualMachines/amazon-corretto-8.jdk/Contents/Home/jre/lib/jsse.jar:/Library/Java/JavaVirtualMachines/amazon-corretto-8.jdk/Contents/Home/jre/lib/jce.jar:/Library/Java/JavaVirtualMachines/amazon-corretto-8.jdk/Contents/Home/jre/lib/charsets.jar:/Library/Java/JavaVirtualMachines/amazon-corretto-8.jdk/Contents/Home/jre/lib/jfr.jar:/Library/Java/JavaVirtualMachines/amazon-corretto-8.jdk/Contents/Home/jre/classes, java.vendor=Amazon.com Inc., file.separator=/, java.vendor.url.bug=https://github.com/corretto/corretto-8/issues/, sun.io.unicode.encoding=UnicodeBig, sun.cpu.endian=little, socksNonProxyHosts=local|*.local|169.254/16|*.169.254/16|127.0.0.1:21300|*.127.0.0.1:21300|localhost|*.localhost|lx.astxsvc.com|*.lx.astxsvc.com, ftp.nonProxyHosts=local|*.local|169.254/16|*.169.254/16|127.0.0.1:21300|*.127.0.0.1:21300|localhost|*.localhost|lx.astxsvc.com|*.lx.astxsvc.com, sun.cpu.isalist=}
    // in Native: {java.specification.version=17, org.graalvm.nativeimage.kind=executable, sun.jnu.encoding=UTF-8, java.class.path=, user.name=appkr, path.separator=:, java.vm.vendor=Oracle Corporation, sun.arch.data.model=64, os.version=12.6, java.endorsed.dirs=, file.encoding=UTF-8, java.vendor.url=https://www.graalvm.org/, java.vm.name=Substrate VM, java.vm.specification.version=17, os.name=Mac OS X, jdk.lang.Process.launchMechanism=FORK, java.io.tmpdir=/var/folders/w3/p26pq0hs3_15szf3sdstvcl40000gn/T/, java.version=17.0.4, user.home=/Users/appkr, user.dir=/Users/appkr/Desktop, os.arch=amd64, java.specification.vendor=Oracle Corporation, java.vm.specification.name=Java Virtual Machine Specification, org.graalvm.nativeimage.imagecode=runtime, file.separator=/, line.separator= , java.library.path=., java.vendor=Oracle Corporation, java.vm.specification.vendor=Oracle Corporation, java.specification.name=Java Platform API Specification, java.vm.version=GraalVM 22.2.0 Java 17 CE, java.ext.dirs=, java.class.version=61.0}
    final String property = System.getProperty("org.graalvm.nativeimage.kind");
    if (property == null) {
      return false;
    }

    return property.equals("executable");
  }
}
