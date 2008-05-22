
# Scans files - with a given extension - recursively from the current 
# directory replacing the old copyright/license header statement with a 
# new version
#
# Example command line usage
# >ruby replaceheaders.rb java oldheader.txt newheader.txt 

class TestMethod
  attr_accessor :text, :parent
  
  def initialize(text, parent)
    @text = text
    @parent = parent
  end  
  
  def name
    #puts @text
    regex = /\S*\(\) /
    @text[regex]
  end
  
  def package_name
    @parent.package_name
  end
  
  def long_name
    #puts "Here's the package name =>" + self.package_name 
    #puts "Here's the self name =>" + self.name 
    self.package_name + "." + self.name
  end
end

class TestCase
  attr_accessor :text, :test_methods
  
  def initialize(text)
    @text = text
    @test_methods = Array.new
    create_test_methods
  end  
  
  def package_name
    line = @text[/pack.*$/]
    line.sub!(/package /, '')
    line.sub(/;/, '')
  end
  
  def testcase_name
    text[/\S*TestCase/]
  end
  
  def create_test_methods
    #/**
    #regex = /Line.*?\{/m
    regex = /\/\*\*.*?$\W*?Line.*?\{/m
   
    test_method_text_array = text.scan(regex)
    test_method_text_array.each {|t| @test_methods<<TestMethod.new(t, self)}
  end
  
end

  
  def process_file(fn)
    text = String.new
    File.open(fn) { |f|  text = f.read } 
    $testcases << TestCase.new(text)
  end
 
$num_files_processed = 0
$testcases = Array.new

puts "Scanning files with .java extension"

Dir["**/*TestCase.java"].each do |filename|
  process_file(filename)
   $num_files_processed += 1
end

puts "Total files processed = #{$num_files_processed}"
puts "Number of test cases = #{$testcases.length}"
$testcases.each do |tc| 
  puts "Next Test Class =>" + tc.package_name + tc.testcase_name
  tc.test_methods.each {|m| puts "  " + m.long_name}
end





