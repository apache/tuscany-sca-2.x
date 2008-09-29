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
}

/**
 * Class that defines an Name represented as a string,
 */
function Name( value ) {
   this.value = value;
   this.getValue = function() {
      return this.value;
   };

   this.setValue = function(value) {
      this.value = value;
   };

   this.toString = function() {
       return "Name value=" + this.value;
   };
}

/**
 * Class that defines a Text object.
 */
function Text( content, /* optional */ type ) {
   this.content = content;
   this.type = type;
   if (!type) this.type = "text"; // If undefined or null, use text
   
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
}

/**
 * Class that defines a Person object.
 * atomPersonConstruct =
 *     atomCommonAttributes,
 *     (element atom:name { text }
 *      & element atom:uri { atomUri }?
 *      & element atom:email { atomEmailAddress }?
 *      & extensionElement*) 
 */
function Person( name, email ) {
   this.name = new Name( name );
   this.email = new Email( email );
   
   this.setName = function( name ) {
      this.name = new Name( name );
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
}

/**
 * Class that defines a Generator object.
 */
function Generator( name, uri ) {
   this.name = new Name( name );
   this.uri = new Uri( uri );
   
   this.setName = function( name ) {
      this.name = new Name( name );
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
function Category( label, uri ) {
   this.label = new Label( label );
   
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

   this.toString = function() {
       return "Category label=" + this.label;
   };
}

/**
 * Class that defines a Link object.
 */
function Link( href ) {
   this.href = new Uri( href );
   
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
   
   var authors = new Array();
   var contributors = new Array();
   var categories = new Array();
   var links = new Array();

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

   this.setNamespace = function( namespace ) {
      this.namespace = namespace;
   };

   this.getNamespace = function() {
      return this.namespace;
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
         error( "Entry rights must be of type Text" );
      this.rights = rights;
   }

   this.getRights = function() {
      return this.rights;
   };

   /* Type Text */
   this.setSummary = function( summary ) {
      this.summary = summary;
   }

   this.getSummary = function() {
      return this.summary;
   };

   /* Type Text */
   this.setTitle = function( title ) {
      if (!((typeof title == "object") && (title instanceof Text)))
         error( "Entry title must be of type Text" );
      this.title = title;
   }

   this.getTitle = function() {
      return this.title;
   };

   /* Type Id */
   this.setId = function( id ) {
      this.id = id;
   }

   this.getId = function() {
      return this.id;
   };

   /**
    * Add an author.
    * @param name Author
    */
   this.addAuthor = function(person) {
      if (!((typeof person == "object") && (person instanceof Person)))
         error( "Entry author must be of type Person" );
      var i = authors.length;
      authors[ i ] = person;
   }
   
   /**
    * Get an author.
    * @param name Author
    */
   this.getAuthor = function(name) {
      return authors[ name ];
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
      return authors;
   }
   
   /**
    * Add an contributor.
    * @param name Contributor
    */
   this.addContributor = function(person) {
      if (!((typeof person == "object") && (person instanceof Person)))
         error( "Entry contributor must be of type Person" );
      var i = contributors.length;
      contributors[ i ] = person;
   }
   
   /**
    * Get an contributor.
    * @param name Contributor
    */
   this.getContributor = function(name) {
      return contributors[ name ];
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
      return contributors;
   }
   
   /**
    * Add an contributor.
    * @param name Category
    */
   this.addCategory = function(category) {
      if (!((typeof category == "object") && (person instanceof Category)))
         error( "Entry category must be of type Category" );
      var i = categories.length
      categories[ i ] = category;
   }
   
   /**
    * Get an contributor.
    * @param name Category
    */
   this.getCategory = function(name) {
      return categories[ name ];
   }
   
   /**
    * Set list of categories
    * @param name Author
    */
   this.setCategories = function( categories ) {
      return this.categories = categories;
   }
   
   /**
    * Get an contributor.
    * @param name Category
    */
   this.getCategories = function() {
      return categories;
   }
   
   /**
    * Add an link.
    * @param name Link
    */
   this.addLink = function(link) {
      if (!((typeof link == "object") && (link instanceof Link)))
         error( "Entry link must be of type Link" );
      var i = links.length;
      links[ i ] = link;
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
               this.setSubTitle( title );
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
   
   // Initialize from constructor   
   if (typeof init == 'object') {
      if ( init.nodeType == 1 ) { /* Document Node.ELEMENT_NODE 1 */
         this.readFromDoc( init );
      } else {      
         error( "Feed init unknown type" );
      }
   }  
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

   var authors = new Array();
   var contributors = new Array();
   var categories = new Array();
   var links = new Array();
   var entries = new Array();

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

   this.setNamespace = function( namespace ) {
      this.namespace = namespace;
   };

   this.getNamespace = function() {
      return this.namespace;
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
         error( "Feed rights must be of type Text" );
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
         error( "Feed title must be of type Text" );
      this.title = title;
   }

   this.getTitle = function() {
      return this.title;
   };

   this.setSubTitle = function( subtitle ) {
      if (!((typeof subtitle == "object") && (subtitle instanceof Text)))
         error( "Feed subtitle must be of type Text" );
      this.subtitle = subtitle;
   }

   this.getSubTitle = function() {
      return this.subtitle;
   };

   /* Type Id */
   this.setId = function( id ) {
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
      var i = authors.length;
      authors[ i ] = person;
   }
   
   /**
    * Get an author.
    * @param name Author
    */
   this.getAuthor = function(name) {
      return authors[ name ];
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
      return authors;
   }
   
   /**
    * Add an contributor.
    * @param name Contributor
    */
   this.addContributor = function(person) {
      if (!((typeof person == "object") && (person instanceof Person)))
         error( "Entry contributor must be of type Person" );
      var i = contributors.length;
      contributors[ i ] = person;
   }
   
   /**
    * Get an contributor.
    * @param name Contributor
    */
   this.getContributor = function(name) {
      return contributors[ name ];
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
      return contributors;
   }
   
   /**
    * Add an contributor.
    * @param name Category
    */
   this.addCategory = function(category) {
      if (!((typeof category == "object") && (person instanceof Category)))
         error( "Entry category must be of type Category" );
      var i = categories.length;
      categories[ i ] = category;
   }
   
   /**
    * Get an contributor.
    * @param name Category
    */
   this.getCategory = function(name) {
      return categories[ name ];
   }
   
   /**
    * Set list of categories
    * @param name Author
    */
   this.setCategories = function( categories ) {
      return this.categories = categories;
   }
   
   /**
    * Get an contributor.
    * @param name Category
    */
   this.getCategories = function() {
      return categories;
   }
   
   /**
    * Add an link.
    * @param name Link
    */
   this.addLink = function(link) {
      if (!((typeof link == "object") && (link instanceof Link)))
         error( "Entry link must be of type Link" );
      var i = links.length
      links[ i ] = link;
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
   
   /**
    * Add an entry.
    * @param name Entry
    */
   this.addEntry = function(entry) {
      if (!((typeof entry == "object") && (entry instanceof Entry)))
         error( "Entry entry must be of type Entry" );
      var i = entries.length;
      entries[ i ] = entry;
   }
   
   /**
    * Get an entry by name.
    * @param name Entry
    */
   this.getEntry = function(name) {
      return entries[ name ];
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
      return entries;
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
               this.setSubTitle( title );
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

   // Initialize from constructor   
   if (typeof init == 'object') {
      if ( init.nodeType == 9 ) { /* Document Node.DOCUMENT_NODE 9 */
         this.readFromDoc( init );
      } else {      
         error( "Feed init unknown type" );
      }
   }  
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