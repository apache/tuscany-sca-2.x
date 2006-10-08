class HelloWorldPropertyTest
	attr_writer :GREETING

	def sayHello(s) 
	    return @GREETING + " " +  s;
	end
end