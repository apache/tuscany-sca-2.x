$SCA = {
   'javaInterface' => 'helloworld.HelloWorldService'
}

def sayHello(s)
	return "Hello to " + s + " from the Ruby World!"
end

class HelloWorldServiceRubyImpl
	def sayHello(s) 
		return "Hello to " + s + " from the Ruby World!"
	end
end
