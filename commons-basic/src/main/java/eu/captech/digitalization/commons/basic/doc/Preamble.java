package eu.captech.digitalization.commons.basic.doc;


import java.lang.annotation.Documented;

@Documented
public @interface Preamble {
    String creationDate();

    String creationTime();

    String lastModified() default "N/A";

    String lastModifiedTime() default "N/A";

    String lastModifiedBy();

    String owner() default "Capture Technologies";

    String author() default "Eduardo Melgar";

    String currentRevision() default "1.1";

    String[] reviewers() default {"eme"};
}
