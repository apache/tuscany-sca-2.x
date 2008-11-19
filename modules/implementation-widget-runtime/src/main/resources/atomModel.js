/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
 
/**
 * Class that defines a URI represented as a string,
 */
function Uri( value ) {
   this.value = value;
   this.getValue = function() {
      return this.value;
   };

   this.setValue = function(value) {
      this.value = value;
   };

   this.toString = function() {
       return "Uri value=" + this.value;
   };

   /**
    * Serialize this element to XML.
    * atomUri = text
    */
   this.toXML = function() {
      xml = "<uri>";
      xml += this.value;
      xml += "</uri>\n";      
      return xml;
   };
}

/* Updated is Date */
/* Published is Date */

/**
 * Class that defines an Email represented as a string,
 */
function Email( value ) {
   this.value = value;
   this.getValue = function() {
      return this.value;
   };

   this.setValue = function(value) {
      this.value = value;
   };

   this.toString = function() {
       return "Email value=" + this.value;
   };

   /**
    * Serialize this element to XML.
    * atomEmailAddress = xsd:string { pattern = ".+@.+" }
    */
   this.toXML = function() {
      xml = "<email>";
      xml += this.value;
      xml += "</email>\n";      
      return xml;
   };
}

/**
 * Class that defines an Id represented as a string,
 */
function Id( value ) {
   this.value = value;
   this.getValue = function() {
      return this.value;
   };

   this.setValue = function(value) {
      this.value = value;
   };

   this.toString = function() {
       return "Id value=" + this.value;
   };

   /**
    * Serialize this element to XML.  
    * atomId = element atom:id {
    *    atomCommonAttributes,
    *    (atomUri)
    * }
    */
   this.toXML = function() {
      xml = "<id";
      if ( this.uri != null ) {
         xml += " uri=\"" + this.uri + "\"";
      }
      if ( this.lang != null ) {
         xml += " lang=\"" + this.lang + "\"";
      }
      xml += ">";
      xml += this.value;
      xml += "</id>\n";      
      return xml;
   };
}


/**
 * Class that defines an Id represented as a string,
 */
function Logo( value ) {
   this.value = value;
   this.getValue = function() {
      return this.value;
   };

   this.setValue = function(value) {
      this.value = value;
   };


   this.toString = function() {
       return "Logo value=" + this.value;
   };

   /**
    * Serialize this element to XML.  
    * atomLogo = element atom:logo {
    *    atomCommonAttributes,
    *    (atomUri)
    * }
    */
   this.toXML = function() {
      xml = "<logo";
      if ( this.uri != null ) {
         xml += " uri=\"" + this.uri + "\"";
      }
      if ( this.lang != null ) {
         xml += " lang=\"" + this.lang + "\"";
      }
      xml += ">";
      xml += this.value;
      xml += "</logo>\n";      
      return xml;
   };
}

/**
 * Class that defines a Text object.
 */
function Text( content, /* optional */ type ) {
   this.content = content;
   this.type = type;
   if (type == null) this.type = "text"; // If undefined or null, use text
   
   this.setText = function(content) {
      this.content = content;
   };

   this.getText = function() {
      return this.content;
   };

   this.getValue = function() {
      return this.content;
   };

   this.setType = function(type) {
      if ((type != "text") && (type != "html") && (type != "xhtml")) {
         error( "Text type must be one of text, html, or xhtml" );
      }
      this.type = type;
   };

   this.getType = function() {
      return this.type;
   };

   this.setLang = function(lang) {
      this.lang = lang;
   };

   this.getLang = function() {
      return this.lang;
   };

   this.setUri = function(uri) {
      this.uri = new Uri( uri );
   };

   this.getUri = function() {
      return this.uri;
   };

   this.toString = function() {
       return "Text type=" + this.type + ", content=" + this.content;
   };
   
   /** Serialize this text element to XML. 
    *  atomPlainTextConstruct =
    *     atomCommonAttributes,
    *     attribute type { "text" | "html" }?,
    *     text
    *
    *  atomXHTMLTextConstruct =
    *     atomCommonAttributes,
    *     attribute type { "xhtml" },
    *     xhtmlDiv
    * 
    *  atomTextConstruct = atomPlainTextConstruct | atomXHTMLTextConstruct
    */
   this.toXML = function( elementName ) {
      if ( elementName == null ) {
         elementName = "text";
      }
      xml = "<" + elementName;
      if ( this.uri != null ) {
         xml += " uri=\"" + this.uri + "\"";
      }
      if ( this.lang != null ) {
         xml += " lang=\"" + this.lang + "\"";
      }
      xml += " type=\"" + this.type + "\"";
      xml += ">";
      if ( this.type === "xhtml" ) {
         xml += "<div xmlns=\"http://www.w3.org/1999/xhtml\">";      
      }
      xml += this.content;
      if ( this.type === "xhtml" ) {
         xml += "</div>";      
      }
      xml += "</" + elementName + ">";      
      return xml;
   }
}

/**
 * Class that defines a Person object.
 */
function Person( name, email ) {
   this.name = name;
   if ( email != null ) {
      this.email = new Email( email );
   }
   
   this.setName = function( name ) {
      this.name = name;
   };

   this.getName = function() {
      return this.name;
   };

   this.setLang = function(lang) {
      this.lang = lang;
   };

   this.getLang = function() {
      return this.lang;
   };

   this.setEmail = function( email ) {
      this.email = new Email( email );
   };

   this.getEmail = function() {
      return this.email;
   };

   this.setUri = function( uri ) {
      this.uri = new Uri( uri );
   };

   this.getUri = function() {
      return this.uri;
   };

   this.toString = function() {
       return "Person name=" + this.name + ", email=" + this.email;
   };

  /** Serialize this text element to XML. 
    * atomPersonConstruct =
    *     atomCommonAttributes,
    *     (element atom:name { text }
    *      & element atom:uri { atomUri }?
    *      & element atom:email { atomEmailAddress }?
    *      & extensionElement*) 
    */
   this.toXML = function( elementName ) {
      if ( elementName == null ) {
         elementName = "person";
      }
      xml = "<" + elementName;
      if ( this.uri != null ) {
         xml += " uri=\"" + this.uri + "\"";
      }
      if ( this.lang != null ) {
         xml += " lang=\"" + this.lang + "\"";
      }
      xml += ">\n";
      if ( this.name != null ) {
         xml += "<name>" + this.name + "</name>\n";
      }
      if ( this.uri != null ) {
         xml += "<uri>" + this.uri + "</uri>\n";
      }
      if ( this.email != null) {
         xml += this.email.toXML();
      }      
      xml += "</" + elementName + ">\n";      
      return xml;
   }
 }

/**
 * Class that defines a Generator object.
 */
function Generator( name, uri ) {
   this.name = name;
   this.uri = new Uri( uri );
   
   this.setName = function( name ) {
      this.name = name;
   };

   this.getName = function() {
      return this.name;
   };

   this.setVersion = function(version) {
      this.version = version;
   };

   this.getVersion = function() {
      return this.version;
   };

   this.setUri = function( uri ) {
      this.uri = new Email( uri );
   };

   this.getUri = function() {
      return this.uri;
   };

   this.toString = function() {
       return "Generator name=" + this.name + ", email=" + this.email;
   };

  /** Serialize this text element to XML. 
    * atomGenerator = element atom:generator {
    *    atomCommonAttributes,
    *    attribute uri { atomUri }?,
    *    attribute version { text }?,
    *    text
    * }
    */
   this.toXML = function() {
      xml = "<generator";
      if ( this.uri != null ) {
         xml += " uri=\"" + this.uri.getValue() + "\"";
      }
      if ( this.lang != null) {
         xml += " lang=\"" + this.lang + "\"";
      }
      if ( this.version != null ) {
         xml += " version=\"" + this.version + "\"";
      }
      xml += ">";
      if ( this.name != null ) {
         xml += this.name;
      }
      xml += "</generator>\n";      
      return xml;
   }
}

/**
 * Class that defines a Category object.
 *atomCategory =
 *     element atom:category {
 *        atomCommonAttributes,
 *        attribute term { text },
 *        attribute scheme { atomUri }?,
 *        attribute label { text }?,
 *        undefinedContent
 *     }
 */
function Category( label, content ) {
   this.label = label;
   this.content = content;
   
   this.setLabel = function( label ) {
      this.label = label;
   };

   this.getLabel = function() {
      return this.label;
   };

   this.setLang = function(lang) {
      this.lang = lang;
   };

   this.getLang = function() {
      return this.lang;
   };

   this.setTerm = function(term) {
      this.term = term;
   };

   this.getTerm = function() {
      return this.term;
   };

   this.setScheme = function( scheme ) {
      this.scheme = scheme;
   };

   this.getScheme = function() {
      return this.scheme;
   };

   this.setContent = function( content ) {
      this.content = content;
   };

   this.getContent = function() {
      return this.content;
   };

   this.toString = function() {
       return "Category label=" + this.label;
   };

   /** Serialize this text element to XML. 
    *  atomCategory =
    *     element atom:category {
    *        atomCommonAttributes,
    *        attribute term { text },
    *        attribute scheme { atomUri }?,
    *        attribute label { text }?,
    *        undefinedContent
    *     }
    */
   this.toXML = function() {
      xml = "<category>\n";
      if ( this.uri != null ) {
         xml += " uri=\"" + this.uri + "\"";
      }
      if ( this.lang != null) {
         xml += " lang=\"" + this.lang + "\"";
      }
      if ( this.term != null) {
         xml += " term=\"" + this.term + "\"";
      }
      if ( this.scheme != null) {
         xml += " scheme=\"" + this.scheme + "\"";
      }
      if ( this.label != null) {
         xml += " label=\"" + this.label + "\"";
      }
      xml += ">\n";
      if ( this.content != null ) {
         xml += this.content + "\n";
      }
      xml += "</category>\n";      
      return xml;
   }
}

/**
 * Class that defines a Link object.
 */
function Link( href, relation ) {
   this.href = new Uri( href );
   this.relation = relation;
   
   this.setHRef = function( uri ) {
      this.href = new Uri( uri );
   };

   this.getHRef = function() {
      return this.href;
   };

   this.setTitle = function( title ) {
      this.title = title;
   };

   this.getTitle = function() {
      return this.title;
   };

   this.setHRefLang = function(lang) {
      this.hrefLang = lang;
   };

   this.getHRefLang = function() {
      return this.hreflang;
   };

   this.setTitleLang = function(lang) {
      this.titleLang = lang;
   };

   this.getTitleLang = function() {
      return this.titleLang;
   };
   
   this.setLength= function( length ) {
      this.length= length;
   };

   this.getLength = function() {
      return this.length;
   };

/*
<static>  <final> String 	TYPE_ATOM
          Link type used for Atom content.
<static>  <final> String 	TYPE_HTML
          Link type used for HTML content.
*/          
   this.setMimeType = function(mimeType) {
      this.mimeType = mimeType;
   };

   this.getMimeType = function() {
      return this.mimeType;
   };

   this.setContent= function( content ) {
      this.content = content;
   };

   this.getContent = function() {
      return this.content;
   };

/*
<static>  <final> String 	REL_ALTERNATE
          Link that provides the URI of an alternate format of the entry's or feed's contents.
<static>  <final> String 	REL_ENTRY_EDIT
          Link that provides the URI that can be used to edit the entry.
<static>  <final> String 	REL_MEDIA_EDIT
          Link that provides the URI that can be used to edit the media associated with an entry.
<static>  <final> String 	REL_NEXT
          Link that provides the URI of next page in a paged feed.
<static>  <final> String 	REL_PREVIOUS
          Link that provides the URI of previous page in a paged feed.
<static>  <final> String 	REL_RELATED
          Link that provides the URI of a related link to the entry.
<static>  <final> String 	REL_SELF
          Link that provides the URI of the feed or entry.
<static>  <final> String 	REL_VIA
          Link that provides the URI that of link that provides the data for the content in the feed.
*/          
   this.setRelation = function( relation ) {
      this.relation = relation;
   };

   this.getRelation = function() {
      return this.relation;
   };

   this.toString = function() {
       return "Link href=" + this.href + ", title=" + this.title;
   };
   
   /** Serialize this text element to XML. 
     * atomLink =
     *     element atom:link {
     *     atomCommonAttributes,
     *     attribute href { atomUri },
     *     attribute rel { atomNCName | atomUri }?,
     *     attribute type { atomMediaType }?,
     *     attribute hreflang { atomLanguageTag }?,
     *     attribute title { text }?,
     *     attribute length { text }?,
     *     undefinedContent
     *  }
     */
   this.toXML = function() {
      xml = "<link";
      if ( this.relation != null ) {
         xml += " rel=\"" + this.relation + "\"";
      }
      if ( this.uri != null ) {
         xml += " uri=\"" + this.uri.getValue() + "\"";
      }
      if ( this.lang != null) {
         xml += " lang=\"" + this.lang + "\"";
      }
      if ( this.href != null ) {
         xml += " href=\"" + this.href.getValue() + "\"";
      }
      if ( this.hreflang != null ) {
         xml += " hreflang=\"" + this.hreflang + "\"";
      }
      if ( this.title != null ) {
         xml += " title=\"" + this.title + "\"";
      }
      if ( this.length != null ) {
         xml += " length=\"" + this.length + "\"";
      }
      if ( this.content != null ) {
         xml += this.content + "\n";
         xml += "</link>\n";
      } else {
         xml += "/>\n";
      }      
      return xml;
   }
   
}          

/**
 * Class that defines an Entry object.
 * atomEntry =
 *     element atom:entry {
 *        atomCommonAttributes,
 *        (atomAuthor*
 *         & atomCategory*
 *         & atomContent?
 *         & atomContributor*
 *         & atomId
 *         & atomLink*
 *         & atomPublished?
 *         & atomRights?
 *         & atomSource?
 *         & atomSummary?
 *         & atomTitle
 *         & atomUpdated
 *         & extensionElement*)
 *     } 
 */
function Entry( init ) {
   // Constructor code at bottom after function definition
   
   this.authors = new Array();
   this.contributors = new Array();
   this.categories = new Array();
   this.links = new Array();

   this.setNamespace = function( namespace ) {
      this.namespace = namespace;
   };

   this.getNamespace = function() {
      return this.namespace;
   };

   this.setId = function( id ) {
      if (!((typeof id == "object") && (id instanceof Id)))
         this.id = new Id( id );
      else 
         this.id = id;
   }

   this.getId = function() {
      return this.id;
   };

   this.setPublished = function( published ) {
      this.published = published;
   };

   this.getPublished = function() {
      return this.published;
   };

   this.setUpdated = function( updated ) {
      this.updated = updated;
   };

   this.getUpdated = function() {
      return this.updated;
   };

   this.setRights = function( rights ) {
      this.rights = rights;
   }

   this.getRights = function() {
      return this.rights;
   };

   this.setSource = function( source ) {
      this.source = source;
   }

   this.getSource = function() {
      return this.source;
   };

   /* Type Text */
   this.setTitle = function( title ) {
      if (!((typeof title == "object") && (title instanceof Text)))
         this.title = new Text( title, "text" );
      else 
         this.title = title;
   }

   this.getTitle = function() {
      return this.title;
   };

   /* Type Text */
   this.setSummary = function( summary ) {
      if (!((typeof summary == "object") && (summary instanceof Text)))
         this.summary = new Text( summary, "text" );
      else
         this.summary = summary;
   }

   this.getSummary = function() {
      return this.summary;
   };

   /* Type Text */
   this.setContent = function( content ) {
      if (!((typeof content == "object") && (content instanceof Text)))
         this.content = new Text( content, "text" );
      else
         this.content = content;
   }

   this.getContent = function() {
      return this.content;
   };

   /**
    * Add an author.
    * @param name Author
    */
   this.addAuthor = function(person) {
      if (!((typeof person == "object") && (person instanceof Person)))
         error( "Entry author must be of type Person" );
      var i = this.authors.length;
      this.authors[ i ] = person; 
   }
   
   /**
    * Get an author.
    * @param name Author
    */
   this.getAuthor = function(name) {
      return this.authors[ name ];
   }
   
   /**
    * Set list of authors.
    * @param name Author
    */
   this.setAuthors = function( authors ) {
      return this.authors = authors;
   }
   
   /**
    * Get an author"pom.xml".
    * @param name Author
    */
   this.getAuthors = function() {
      return this.authors;
   }
   
   /**
    * Add an contributor.
    * @param name Contributor
    */
   this.addContributor = function(person) {
      if (!((typeof person == "object") && (person instanceof Person)))
         error( "Entry contributor must be of type Person" );
      var i = this.contributors.length;
      this.contributors[ i ] = person;
   }
   
   /**
    * Get an contributor.
    * @param name Contributor
    */
   this.getContributor = function(name) {
      return this.contributors[ name ];
   }
   
   /**
    * Set list of contributors
    * @param name Author
    */
   this.setContributors = function( contributors ) {
      return this.contributors = contributors;
   }
   
   /**
    * Get an contributor.
    * @param name Contributor
    */
   this.getContributors = function() {
      return this.contributors;
   }
   
   /**
    * Add a category.
    * @param name Category
    */
   this.addCategory = function(category) {
      if (!((typeof category == "object") && (category instanceof Category)))
         error( "Entry category must be of type Category" );
      var i = this.categories.length
      this.categories[ i ] = category;
   }
   
   /**
    * Get a names category.
    * @param name Category
    */
   this.getCategory = function(name) {
      return this.categories[ name ];
   }
   
   /**
    * Set list of categories
    * @param name Author
    */
   this.setCategories = function( categories ) {
      return this.categories = categories;
   }
   
   /**
    * Get all categories.
    * @param name Category
    */
   this.getCategories = function() {
      return this.categories;
   }
   
   /**
    * Add an link.
    * @param name Link
    */
   this.addLink = function(link) {
      if (!((typeof link == "object") && (link instanceof Link)))
         error( "Entry link must be of type Link" );
      var i = this.links.length;
      this.links[ i ] = link;
   }
   
   /**
    * Get an link.
    * @param name Link
    */
   this.getLink = function(name) {
      return links[ name ];
   }
   
   /**
    * Set list of links.
    * @param name Link
    */
   this.setLinks = function( links ) {
      return this.links = links;
   }
   
   /**
    * Get an link.
    * @param name Link
    */
   this.getLinks = function() {
      return links;
   }
   
   this.readFromXML = function( xml ) {
      if (!((typeof xml == "object") && (xml instanceof string)))
         error( "Entry xml must be of type string" );
      // To Do - Read from arbutrary XML such as 
      // <entry>
      //   <title type="text">cart-item</title>
      //   <content type="text">Apple - $ 2.99</content>
      //   <id>cart-bd5323d6-1f59-4fae-a8f5-01f7654f1e77</id>
      //   <link href="cart-bd5323d6-1f59-4fae-a8f5-01f7654f1e77" rel="edit"/>
      //   <link href="cart-bd5323d6-1f59-4fae-a8f5-01f7654f1e77" rel="alternate"/>
      //   <updated>2008-09-21T23:06:43.921Z</updated>
      // </entry>
      
   }
  this.readFromDoc = function( htmlDoc ) {
      // Expect HTML collection.
      var entryDoc = htmlDoc.getElementsByTagName("entry");
      for (var i = 0; i < entryDoc.length; i++) {
         this.readFromNode( entryDoc[ i ] );
      }
   }

   this.readFromNode = function( entryNode ) {
      // Expect entry node
      var childNodes = entryNode.childNodes;
      for ( var i = 0; i < childNodes.length; i++ ) {
         var node = childNodes[ i ];
         if (node.nodeType == 1 /*Node.ELEMENT_NODE*/) {
            var tagName = node.tagName;
            if (tagName == "title" ) {
               var text = getTextContent( node );
               var type = node.getAttribute( "type" );
               if ( type == undefined )
                  type = "text";
               var title = new Text( text, type );
               this.setTitle( title );
            } else if ( tagName == "subtitle" ) {
               var text = getTextContent( node );
               var type = node.getAttribute( "type" );
               if ( type == undefined )
                  type = "text";
               var title = new Text( text, type );
               this.setSubtitle( title );
            } else if ( tagName == "id" ) {
               var id = new Id( getTextContent( node ) );
               this.setId( id );
            } else if ( tagName == "updated" ) {
               var dateText = getTextContent( node );
               var date = new Date( dateText ); // 2008-09-21T23:06:43.921Z
               this.setUpdated( date );
            } else if ( tagName == "link" ) {
               // var href = node.attributes[ "href" ]; // Works on modern browsers.
               var attrVal = node.getAttribute( "href" );
               var link = new Link( attrVal );
               attrVal = node.getAttribute( "rel" );
               if ( attrVal )
                  link.setRelation( attrVal );
               this.addLink( link );
            } else if ( tagName == "content" ) {
               var text = getTextContent( node );
               var type = node.getAttribute( "type" );
               if (type == undefined)
                  type = "text";
               var content = new Text( text, type );
               this.setContent( content );
            } else {
               // To Do - implement rest of nodes
			   error( "undefined element node" );
            } 
         } else if (node.nodeType == 2 /*Node.ATTRIBUTE_NODE*/) {
            var attrName = node.tagName;
         } else if (node.nodeType == 3 /*Node.TEXT_NODE*/) {
         }
      }      
   }

   this.toString = function() {
       return "Entry title=" + this.title + ", updated=" + this.updated;
   };

   /** Serialize this text element to XML. 
	 * atomEntry =
	 *     element atom:entry {
	 *        atomCommonAttributes,
	 *        (atomAuthor*
	 *         & atomContributor*
	 *         & atomCategory*
	 *         & atomLink*
	 *         & atomTitle
	 *         & atomId
	 *         & atomPublished?
	 *         & atomUpdated
	 *         & atomContent?
	 *         & atomRights?
	 *         & atomSource?
	 *         & atomSummary?
	 *         & extensionElement*)
	 *     }    
     */
   this.toXML = function() {
      xml = "<entry";
      if ( this.namespace != null ) {
         xml += " namespace=\"" + this.namespace + "\"";
      }
      if ( this.uri != null ) {
         xml += " uri=\"" + this.uri + "\"";
      }
      if ( this.lang != null ) {
         xml += " lang=\"" + this.lang + "\"";
      }
      xml += ">";
      if ( this.title != null ) {
         xml += this.title.toXML( "title" );
      }
      if ( this.id != null ) {
         xml += this.id.toXML();
      }
      if ( this.published != null ) {
         xml += "<published>" + this.published + "</published>\n";
      }
      if ( this.updated != null ) {
         xml += "<updated>" + this.updated + "</updated>\n";
      }
      if ( this.authors != null ) {
      for ( var i = 0; i < this.authors.length; i++ ) {
         var author = this.authors[ i ];
         xml += author.toXML( "author" );
      }
      }
      if ( this.contributors != null ) {
      for ( var i = 0; i < this.contributors.length; i++ ) {
         var contributor = this.contributors[ i ];
         xml += contributor.toXML( "contributor" );
      }
      }
      if ( this.categories != null ) {
      for ( var i = 0; i < this.categories.length; i++ ) {
         var category = this.categories[ i ];
         xml += category.toXML();
      }
      }
      if ( this.links != null ) {
      for ( var i = 0; i < this.links.length; i++ ) {
         var link = this.links[ i ];
         xml += link.toXML();
      }
      }
      if ( this.rights != null ) {
         xml += "<rights>" + this.rights + "</rights>\n";
      }
      if ( this.source != null ) {
         xml += "<source>" + this.source + "</source>\n";
      }
      if ( this.summary != null ) {
         xml += this.summary.toXML( "summary" );
      }
      if ( this.content != null ) {
         xml += this.content.toXML( "content" );
      }
      xml += "</entry>";      
      return xml;
   }
  
   // Initialize from constructor   
   if (typeof init == 'object') {
      if ( init.nodeType == 1 ) { /* Document Node.ELEMENT_NODE 1 */
         this.readFromDoc( init );
      } else {      
         error( "Feed init unknown type" );
      }
   }  else if ( typeof init === 'string' ) {
      this.setTitle( init );
   }
   this.namespace = "http://www.w3.org/2005/Atom";
}

/**
 * Class that defines an Feed object.
 *  atomFeed =
 *     element atom:feed {
 *        atomCommonAttributes,
 *        (atomAuthor*
 *         & atomCategory*
 *         & atomContributor*
 *         & atomGenerator?
 *         & atomIcon?
 *         & atomId
 *         & atomLink*
 *         & atomLogo?
 *         & atomRights?
 *         & atomSubtitle?
 *         & atomTitle
 *         & atomUpdated
 *         & extensionElement*),
 *        atomEntry* 
 */
function Feed( init ) {
   // See init after functions have been defined.

   this.authors = new Array();
   this.contributors = new Array();
   this.categories = new Array();
   this.links = new Array();
   this.entries = new Array();

   this.setNamespace = function( namespace ) {
      this.namespace = namespace;
   };

   this.getNamespace = function() {
      return this.namespace;
   };

   this.setPublished = function( published ) {
      this.published = published;
   };

   this.getPublished = function() {
      return this.published;
   };

   this.setUpdated = function( updated ) {
      this.updated = updated;
   };

   this.getUpdated = function() {
      return this.updated;
   };

   this.setContent = function( content ) {
      if (!((typeof content == "object") && (content instanceof Text)))
         error( "Entry content must be of type Text" );

      this.content = content;
   }

   this.getContent = function() {
      return this.content;
   };

   this.setRights = function( rights ) {
      if (!((typeof rights == "object") && (rights instanceof Text)))
         this.rights = new Text( rights, "text" );
      else 
         this.rights = rights;
   }

   this.getRights = function() {
      return this.rights;
   };

   this.setSummary = function( summary ) {
      if (!((typeof summary == "object") && (summary instanceof Text)))
         error( "Feed summary must be of type Text" );
      this.summary = summary;
   }

   this.getSummary = function() {
      return this.summary;
   };

   this.setTitle = function( title ) {
      if (!((typeof title == "object") && (title instanceof Text)))
         this.title = new Text( title, "text" );
      else 
         this.title = title;
   }

   this.getTitle = function() {
      return this.title;
   };

   this.setSubtitle = function( subtitle ) {
      if (!((typeof subtitle == "object") && (subtitle instanceof Text)))
         this.subtitle = new Text( subtitle, "text" );
      else 
         this.subtitle = subtitle;
   }

   this.getSubtitle = function() {
      return this.subtitle;
   };

   /* Type Id */
   this.setId = function( id ) {
      if (!((typeof id == "object") && (id instanceof Id)))
         this.id = new Id( id );
      else 
         this.id = id;
   }

   this.getId = function() {
      return this.id;
   };

   this.setGenerator = function( generator ) {
      if (!((typeof generator == "object") && (generator instanceof Generator)))
         error( "Feed generator must be of type Generator" );
      this.generator = generator;
   }

   this.getGenerator = function() {
      return this.generator;
   };

   this.setBase = function( base ) {
      this.base = base;
   }

   this.getBase = function() {
      return this.base;
   };

   this.setLogo = function( logo ) {
      this.logo = logo;
   }

   this.getLogo = function() {
      return this.logo;
   };

   /**
    * Add an author.
    * @param name Author
    */
   this.addAuthor = function(person) {
      if (!((typeof person == "object") && (person instanceof Person)))
         error( "Entry author must be of type Person" );
      var i = this.authors.length;
      this.authors[ i ] = person;
   }
   
   /**
    * Get an author.
    * @param name Author
    */
   this.getAuthor = function(name) {
      return this.authors[ name ];
   }
   
   /**
    * Set list of authors.
    * @param name Author
    */
   this.setAuthors = function( authors ) {
      return this.authors = authors;
   }
   
   /**
    * Get an author.
    * @param name Author
    */
   this.getAuthors = function() {
      return this.authors;
   }
   
   /**
    * Add an contributor.
    * @param name Contributor
    */
   this.addContributor = function(person) {
      if (!((typeof person == "object") && (person instanceof Person)))
         error( "Entry contributor must be of type Person" );
      var i = this.contributors.length;
      this.contributors[ i ] = person;
   }
   
   /**
    * Get an contributor.
    * @param name Contributor
    */
   this.getContributor = function(name) {
      return this.contributors[ name ];
   }
   
   /**
    * Set list of contributors
    * @param name Author
    */
   this.setContributors = function( contributors ) {
      return this.contributors = contributors;
   }
   
   /**
    * Get an contributor.
    * @param name Contributor
    */
   this.getContributors = function() {
      return this.contributors;
   }
   
   /**
    * Add a category.
    * @param name Category
    */
   this.addCategory = function(category) {
      if (!((typeof category == "object") && (category instanceof Category)))
         error( "Feed category must be of type Category" );
      var i = this.categories.length;
      this.categories[ i ] = category;
   }
   
   /**
    * Get a named contributor.
    * @param name Category
    */
   this.getCategory = function(name) {
      return this.categories[ name ];
   }
   
   /**
    * Set list of categories
    * @param category
    */
   this.setCategories = function( categories ) {
      return this.categories = categories;
   }
   
   /**
    * Get all categories.
    * @param name Category
    */
   this.getCategories = function() {
      return this.categories;
   }
   
   /**
    * Add an link.
    * @param name Link
    */
   this.addLink = function(link) {
      if (!((typeof link == "object") && (link instanceof Link)))
         error( "Entry link must be of type Link" );
      var i = this.links.length;
      this.links[ i ] = link;
   }
   
   /**
    * Get an link.
    * @param name Link
    */
   this.getLink = function(name) {
      return this.links[ name ];
   }
   
   /**
    * Set list of links.
    * @param name Link
    */
   this.setLinks = function( links ) {
      return this.links = links;
   }
   
   /**
    * Get an link.
    * @param name Link
    */
   this.getLinks = function() {
      return this.links;
   }
   
   /**
    * Add an entry.
    * @param name Entry
    */
   this.addEntry = function(entry) {
      if (!((typeof entry == "object") && (entry instanceof Entry)))
         error( "Entry entry must be of type Entry" );
      var i = this.entries.length;
      this.entries[ i ] = entry;
   }
   
   /**
    * Get an entry by name.
    * @param name Entry
    */
   this.getEntry = function(name) {
      return this.entries[ name ];
   }
   
   /**
    * Set list of entries
    * @param name Author
    */
   this.setEntries = function( entries ) {
      return this.entries = entries;
   }
   
   /**
    * Get an contributor.
    * @param name Entry
    */
   this.getEntries = function() {
      return this.entries;
   }
   
   this.readFromXML = function( xml ) {
      // To Do Read from arbitraty XML such as 
      // <feed xmlns="http://www.w3.org/2005/Atom">
      // <title type="text">shopping cart</title>
      // <subtitle type="text">Total : $4.54</subtitle>
      // <entry>
      //    ...
      // </entry>
      // </feed>    
   }

   this.readFromDoc = function( htmlDoc ) {
      // Expect HTML collection.
      var feedDoc = htmlDoc.getElementsByTagName("feed");
      for (var i = 0; i < feedDoc.length; i++) {
         this.readFromNode( feedDoc[ i ] );
      }
   }
   this.readFromNode = function( feedNode ) {
      // Expect feed node
      var childNodes = feedNode.childNodes;
      for ( var i = 0; i < childNodes.length; i++ ) {
         var node = childNodes[ i ];
         if (node.nodeType == 1 /*Node.ELEMENT_NODE*/) {
            var tagName = node.tagName;
            if (tagName == "title" ) {
               var text = getTextContent( node );
               var type = node.getAttribute( "type" );
               if ( type == undefined )
                  type = "text";
               var title = new Text( text, type );
               this.setTitle( title );
            } else if ( tagName == "subtitle" ) {
               var text = getTextContent( node );
               var type = node.getAttribute( "type" );
               if ( type == undefined )
                  type = "text";
               var title = new Text( text, type );
               this.setSubtitle( title );
            } else if ( tagName == "entry" ) {
               var entry = new Entry();
               entry.readFromNode( node );
               this.addEntry( entry ); 
            } else if ( tagName == "id" ) {
               var id = new Id( getTextContent( node ) );
               this.setId( id );
            } else if ( tagName == "updated" ) {
               var dateText = getTextContent( node );
               var date = new Date( dateText ); //2008-09-21T23:06:53.750Z
               this.setUpdated( date );
            } else if ( tagName == "link" ) {
               // var href = node.attributes[ "href" ]; // Works on modern browsers.
               var attrVal = node.getAttribute( "href" );
               var link = new Link( attrVal );
               attrVal = node.getAttribute( "rel" );
               if ( attrVal )
                  link.setRelation( attrVal );
               this.addLink( link );
            } else {
               // To Do - implement rest of nodes
			   error( "undefined element node" );
            } 
         } else if (node.nodeType == 2 /*Node.ATTRIBUTE_NODE*/) {
            var attrName = node.tagName;
         } else if (node.nodeType == 3 /*Node.TEXT_NODE*/) {
         }
      }      
   }

   this.toString = function() {
       return "Feed title=" + this.title + ", updated=" + this.updated;
   };

   /** Serialize this text element to XML. 
	 *  atomFeed =
	 *     element atom:feed {
	 *        atomCommonAttributes,
	 *        (atomAuthor*
	 *         & atomContributor*
	 *         & atomCategory*
	 *         & atomLink*
	 *         & atomTitle
	 *         & atomSubtitle?
	 *         & atomId
	 *         & atomUpdated
	 *         & atomRights?	 
	 *         & atomGenerator?
	 *         & atomIcon?
	 *         & atomLogo?
	 *         & extensionElement*),
	 *        atomEntry* 
     */
   this.toXML = function() {
      xml = "<feed";
      if ( this.namespace != null ) {
         xml += " namespace=\"" + this.namespace + "\"";
      }
      if ( this.uri != null ) {
         xml += " uri=\"" + this.uri + "\"";
      }
      if ( this.lang != null ) {
         xml += " lang=\"" + this.lang + "\"";
      }
      xml += ">\n";
      if ( this.title != null ) {
         xml += this.title.toXML( "title" );
      }
      if ( this.subtitle != null ) {
         xml += this.subtitle.toXML( "subtitle" );
      }
      if ( this.id != null ) {
         xml += this.id.toXML();
      }
      if ( this.published != null ) {
         xml += "<published>" + this.published + "</published>\n";
      }
      if ( this.updated != null ) {
         xml += "<updated>" + this.updated + "</updated>\n";
      }
      if ( this.authors != null ) {      
      for ( var i = 0; i < this.authors.length; i++ ) {
         var author = this.authors[ i ];
         xml += author.toXML( "author" );
      }
      }
      if ( this.contributors != null ) {      
      for ( var i = 0; i < this.contributors.length; i++ ) {
         var contributor = this.contributors[ i ];
         xml += contributor.toXML( "contributor" );
      }
      }
      if ( this.categories != null ) {      
      for ( var i = 0; i < this.categories.length; i++ ) {
         var category = this.categories[ i ];
         xml += category.toXML();
      }
      }
      if ( this.links != null ) {      
      for ( var i = 0; i < this.links.length; i++ ) {
         var link = this.links[ i ];
         xml += link.toXML();
      }
      }
      if ( this.rights != null ) {
         xml += this.rights.toXML( "rights" );
      }
      if ( this.source != null ) {
         xml += "<source>" + this.source + "</source>\n";
      }
      if ( this.logo != null ) {
         xml += "<logo>" + this.logo + "</logo>\n";
      }
      if ( this.icon != null ) {
         xml += "<icon>" + this.icon + "</icon>\n";
      }
      if ( this.generator != null ) {
         xml += this.generator.toXML( "generator" );
      }
      if ( this.summary != null ) {
         xml += this.summary.toXML( "summary" );
      }
      if ( this.entries != null ) {
      for ( var i = 0; i < this.entries.length; i++ ) {
         var entry = this.entries[ i ];
         xml += entry.toXML();
      }      
      }      
      xml += "</feed>\n";      
      return xml;
   }
  
   // Initialize from constructor   
   if (typeof init == 'object') {
      if ( init.nodeType == 9 ) { /* Document Node.DOCUMENT_NODE 9 */
         this.readFromDoc( init );
      } else {      
         error( "Feed init unknown type" );
      }
   }  else if ( typeof init === 'string' ) {
      this.setTitle( init );
   }   
   this.namespace = "http://www.w3.org/2005/Atom";
}

function error( message ) {
   alert( message );
}

/* Returns inner text on both IE and modern browsers. */
function getTextContent(node) {
   // innerText for IE, textContent for others, child text node, "" for others.
   if ( node.innerText )
      return node.innerText;
   if ( node.textContent )
      return node.textContent;
   if ( node.hasChildNodes() ) {
      var childNodes = node.childNodes
      for ( var j = 0; j < childNodes.length; j++ ) {
         var childNode = childNodes[ j ];
         var childType = childNode.nodeType;
         if (childNode.nodeType == 3 /*Node.TEXT_NODE*/) {
            return childNode.nodeValue;
         }
      } 
   }
   return undefined;
} 