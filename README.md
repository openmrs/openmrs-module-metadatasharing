[![Build Status](https://travis-ci.org/openmrs/openmrs-module-metadatasharing.svg?branch=master)](https://travis-ci.org/openmrs/openmrs-module-metadatasharing)

OpenMRS - MetadataSharing - Module
==================================

  **The Metadata Sharing Module** allows all kinds of metadata (concepts, htmlforms, locations, roles, programs) to be exchanged between different OpenMRS installations. 
  
  The module supports metadata defined in the core as well as in other modules, provided appropriate handlers are registered. Conflicts between local and incoming metadata can be identified and resolved. The module can be used both through an API and a web interface.


Any dependent metadata will be packaged along with the selected item. For example, if you select a concept which has coded answers, it will package the initial concept along with all the coded answer concepts, class, and datatype. If you select an htmlform, it will package the form along with the encounter type, all concepts used on that form, etc.

See https://wiki.openmrs.org/display/docs/Metadata+Sharing+Module for more information.
