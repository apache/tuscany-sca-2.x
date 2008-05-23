
# Scans files - with a java extension - recursively from the current 
# directory processing test method comments.  Eventually, this will produce a sorted
# list of specification line numbers and their associated test methods
#
# Example command line usage
# >ruby processcomments.rb 

class TestMethod
  attr_accessor :text, :parent
  
  def initialize(text, parent)
    puts "Test method text => " + text
    @text = text
    @parent = parent
  end  
  
  def name
    regex = /void\s*\S*\(\) /
    @text[regex]
  end
  
  def lines_tested
    lines_regex = /Line.*?$/
    lines_array = text.scan(lines_regex)
    nums_regex = /\d{1,4}/
    line_numbers = Array.new
    lines_array.each do |line_text|
      number_strings =line_text.scan(nums_regex)
      number_strings.each {|num_string| line_numbers<<num_string.to_i}
    end
    line_numbers
  end
  
  def lines_tested_string
    lts = String.new
    lines_tested.each {|n| lts = lts +"," + n.to_s }
    lts
  end
  
  def package_name
    @parent.package_name
  end
  
  def long_name
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
  
  def create_test_methods
    #regex = /\/\*\*.*?$\W*?Line.*?\{/m
    regex = /\/\*\*\W*?$\W*?Line.*?\{/m
    test_method_text_array = text.scan(regex)
    test_method_text_array.each do |t| 
      @test_methods<<TestMethod.new(t, self)
      $num_test_methods +=1
    end
  end
  
  def package_name
    line = @text[/pack.*$/]
    line.sub!(/package /, '')
    line.sub(/;/, '')
  end
  
  def testcase_name
    text[/\S*TestCase/]
  end
end

  
def process_file(fn)
  text = String.new
  File.open(fn) { |f|  text = f.read } 
  $testcases << TestCase.new(text)
end
 
$num_files_processed = $num_test_methods = 0
$testcases = Array.new

puts "Scanning files with .java extension"

Dir["**/*TestCase.java"].each do |filename|
  process_file(filename)
   $num_files_processed += 1
end

puts "Total files processed = #{$num_files_processed}"
puts "Number of test cases = #{$testcases.length}"
puts "Number of test methods = #{$num_test_methods}"

$testcases.each do |tc| 
  puts "Next Test Class =>" + tc.package_name + "." + tc.testcase_name
  tc.test_methods.each {|m| puts "  " + m.long_name + "lines => " + m.lines_tested_string }
end

puts "************* Here we go *******************"
$testcases.each do |tc| 
  all_test_methods = Array.new
  tc.test_methods.each {|m| all_test_methods<<m}
  
  
  all_test_methods.sort!{|a, b| a.lines_tested.first <=> b.lines_tested.first}
  all_test_methods.each {|tm| puts tm.lines_tested.first.to_s}
  
  #sorted = all_test_methods.sort_by { |m| m.lines_tested.first }
  #sorted.each {|tm| puts tm.lines_tested.to_s}
end



