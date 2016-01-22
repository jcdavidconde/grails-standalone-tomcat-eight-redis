import ru.zinin.redis.session.RedisManager;

class StandaloneTomcatEightRedisGrailsPlugin {
    // the plugin version
    def version = "0.8.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.5 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "Grails Standalone Tomcat 8 Redis Plugin" // Headline display name of the plugin
    def author = "David Conde"
    def authorEmail = "jcdavidconde@gmail.com"
    def description = 'Uses Redis as the Tomcat session manager when using the Tomcat 8 server'

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/grails-standalone-tomcat-eight-redis"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
       def conf = application.config.grails.plugin.standalone.tomcat.redis

		tomcatSessionManager(RedisManager) {
			disableListeners = true
//			if (conf.dbIndex)       dbIndex       = conf.dbIndex
			if (conf.redisHostname) redisHostname = conf.redisHostname
			if (conf.redisPassword) redisPassword = conf.redisPassword
			if (conf.redisPort)     redisPort     = conf.redisPort
		}
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { ctx ->
        def tomcatSessionManager = ctx.tomcatSessionManager
		if (!tomcatSessionManager) {
			log.debug "No tomcatSessionManager bean found, not updating the Tomcat session manager"
			return
		}

		def servletContext = ctx.servletContext

		try {
			if ('org.apache.catalina.core.ApplicationContextFacade'.equals(servletContext.getClass().name)) {
				def realContext = servletContext.context
				if ('org.apache.catalina.core.ApplicationContext'.equals(realContext.getClass().name)) {
					def standardContext = realContext.@context
					if ('org.apache.catalina.core.StandardContext'.equals(standardContext.getClass().name)) {
						standardContext.manager = tomcatSessionManager
						log.info "Set the Tomcat session manager to $tomcatSessionManager"
					}
					else {
						log.warn "Not updating the Tomcat session manager, the context isn't an instance of org.apache.catalina.core.StandardContext"
					}
				}
				else {
					log.warn "Not updating the Tomcat session manager, the wrapped servlet context isn't an instance of org.apache.catalina.core.ApplicationContext"
				}
			}
			else {
				log.warn "Not updating the Tomcat session manager, the servlet context isn't an instance of org.apache.catalina.core.ApplicationContextFacade"
			}
		}
		catch (Throwable e) {
			log.error "There was a problem changing the Tomcat session manager: $e.message", e
		}
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
