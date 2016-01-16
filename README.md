OpenMRS - MetadataSharing - Module
==================================

  **The Metadata Sharing Module** allows all kinds of metadata (concepts, htmlforms, locations, roles, programs) to be exchanged between different OpenMRS installations. 
  
  This module support's metadata defined in the core as well as in other modules, provided appropriate handlers are registered. Conflicts between local and incoming metadata can be identified and resolved. This module can be used both through an API or a web interface.


Any dependent metadata will be also packaged along with the selected item. For example, if you select a concept which has coded many answers, it will package the initial concept along with all the coded answer concepts, class, and datatype. If you select a HTML form, it will also package the form along with the encounter type, all concepts used in that form etc.

See https://wiki.openmrs.org/display/docs/Metadata+Sharing+Module for more information.
