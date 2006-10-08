class Helloworld

   def sayHello(s) 
	  return "Hello to " + s + " from the Ruby World!"
   end

end

class HelloWorldServiceRubyImpl
	attr_writer :extHelloWorld
	attr_writer :greeting

	def sayHello(s) 
	    return @greeting + " " +  @extHelloWorld.sayHello(s);
	end
end
