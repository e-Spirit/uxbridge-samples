class UrlMappings {

	static mappings = {
		
//		"/rest/v1/newsdrilldown/$id"(controller:"news") {
//			action = [GET:"show"]
//		}
		
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        "500"(view:'/error')
	}
}
